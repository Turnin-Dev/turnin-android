package com.peekr.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSource
import com.peekr.core.data.source.network.dto.feed.toUserKeywordDetailEntity
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.feed.model.Feed
import com.peekr.core.domain.feed.model.FeedCursor
import com.peekr.core.domain.model.UserKeywordId

@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator(
    private val feedNetworkDataSource: FeedNetworkDataSource,
    private val database: PeekrDatabase,
) : RemoteMediator<FeedCursor, Feed>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<FeedCursor, Feed>,
    ): MediatorResult {
        return try {
            val cursor: FeedCursor? = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // 1) 현재 로드된 마지막 아이템 찾기
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    // 2) DB에서 마지막 아이템에 매칭된 리모트 키 조회
                    val remoteKey = database.feedRemoteKeyDao()
                        .getById(userKeywordId = lastItem.userKeywordId.value)

                    // 3) 조회된 커서가 null이면 페이지의 끝
                    if (remoteKey == null ||
                        (
                            remoteKey.cursorScore == null &&
                                remoteKey.cursorCreatedAt == null &&
                                remoteKey.cursorUserKeywordId == null
                        )
                    ) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    // 4) 커서 반환
                    FeedCursor(
                        score = remoteKey.cursorScore,
                        createdAt = remoteKey.cursorCreatedAt,
                        userKeywordId = remoteKey.cursorUserKeywordId?.let {
                            UserKeywordId(it)
                        },
                    )
                }
            }

            val response = feedNetworkDataSource.getFeeds(
                cursorScore = cursor?.score,
                cursorCreatedAt = cursor?.createdAt,
                cursorUserKeywordId = cursor?.userKeywordId?.value,
                size = state.config.pageSize,
            )

            when (response) {
                is NetworkResult.Error -> {
                    MediatorResult.Error(
                        PagingApiCallException(
                            error = response.error.toCommonErrorType(),
                            message = response.message,
                        ),
                    )
                }

                is NetworkResult.Success -> {
                    val data = response.data
                    val nextCursor = data.nextCursor
                    val endOfPaginationReached = nextCursor == null

                    database.withTransaction {
                        // Clear cache
                        if (loadType == LoadType.REFRESH) {
                            database.userKeywordDetailDao().deleteAll()
                            database.feedRemoteKeyDao().deleteAll()
                        }

                        // 데이터 저장
                        database.userKeywordDetailDao().upsertAll(
                            data.items.map { it.toUserKeywordDetailEntity() },
                        )
                        // 서버에서 받은 nextCursor를 키로 저장
                        // 다음 APPEND 요청 시 어떤 아이템에서 출발하든 서버가 준 다음 커서를 알 수 있게 함
                        val remoteKey = FeedRemoteKeyEntity(
                            lastUserKeywordId = data.items.last().userKeywordId,
                            cursorScore = nextCursor?.score,
                            cursorCreatedAt = nextCursor?.createdAt,
                            cursorUserKeywordId = nextCursor?.userKeywordId,
                        )
                        database.feedRemoteKeyDao().upsert(remoteKey)
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
