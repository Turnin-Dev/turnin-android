package com.turnin.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.source.local.database.TurninDatabase
import com.turnin.core.data.source.local.database.entity.FeedEntity
import com.turnin.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSource
import com.turnin.core.data.source.network.dto.feed.toEntity
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.PagingApiCallException
import com.turnin.core.domain.feed.model.FeedCursor
import com.turnin.core.domain.feed.model.FeedType
import kotlinx.coroutines.CancellationException

/**
 * Feed 페이징 RemoteMediator
 */
@OptIn(ExperimentalPagingApi::class)
class FeedRemoteMediator(
    private val feedType: FeedType,
    private val feedNetworkDataSource: FeedNetworkDataSource,
    private val database: TurninDatabase,
) : RemoteMediator<Int, FeedEntity>() {
    private val tag = this::class.java.simpleName

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedEntity>,
    ): MediatorResult {
        return try {
            val cursor: FeedCursor? = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // 1) 현재 로드된 데이터가 없다면, 아직 REFRESH 전이거나 로딩 중일 수 있으므로 false로 반환
                    state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = false)

                    // 2) DB에서 리모트 키 조회
                    val remoteKey = database.feedRemoteKeyDao().getRemoteKeyByType(feedType)

                    // 3) 조회된 커서가 없으면 페이지의 끝
                    if (remoteKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    // 4) 커서 반환
                    FeedCursor(remoteKey.cursor)
                }
            }

            val response = feedNetworkDataSource.getFeeds(
                feedType = feedType,
                cursor = cursor?.cursor,
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
                    val items = data.items
                    val nextCursor = data.nextCursor
                    val endOfPaginationReached = items.isEmpty() || nextCursor == null

                    database.withTransaction {
                        // Clear cache
                        if (loadType == LoadType.REFRESH) {
                            database.feedDao().clearByType(feedType)
                            database.feedRemoteKeyDao().clearByType(feedType)
                        }

                        if (items.isNotEmpty()) {
                            // 피드 유형별 순서 채번
                            val startOrder = if (loadType == LoadType.REFRESH) {
                                0
                            } else {
                                database.feedDao().countByType(feedType)
                            }
                            val entities = items.mapIndexed { index, feedResponse ->
                                feedResponse.toEntity(feedType, startOrder + index)
                            }

                            // 데이터 저장
                            database.feedDao().upsertAll(entities)

                            // 서버에서 받은 nextCursor를 키로 저장
                            // 만약 nextCursor가 null이라면 저장하지 않음
                            if (nextCursor != null) {
                                val remoteKey = FeedRemoteKeyEntity(feedType, nextCursor)
                                database.feedRemoteKeyDao().upsert(remoteKey)
                            } else {
                                database.feedRemoteKeyDao().clearByType(feedType)
                            }
                        }
                    }
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AppLogger.e(tag, e, "RemoteMediator exception during load.")
            MediatorResult.Error(e)
        }
    }
}
