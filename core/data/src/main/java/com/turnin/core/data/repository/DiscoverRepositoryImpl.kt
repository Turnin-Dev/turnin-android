package com.turnin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.data.paging.TurninCursorPagingSource
import com.turnin.core.data.source.local.memory.MemoryCache
import com.turnin.core.data.source.network.datasource.DiscoverNetworkDataSource
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextCursorPageResponse
import com.turnin.core.data.source.network.dto.discover.response.DiscoverContextResponse
import com.turnin.core.data.source.network.dto.discover.response.toDomainModel
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.discover.model.DiscoverCacheKey
import com.turnin.core.domain.discover.model.DiscoverContext
import com.turnin.core.domain.discover.model.DiscoverPagingTokens
import com.turnin.core.domain.discover.repository.DiscoverRepository
import com.turnin.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DiscoverRepositoryImpl @Inject constructor(
    private val discoverNetworkDataSource: DiscoverNetworkDataSource,
    private val memoryCache: MemoryCache<DiscoverCacheKey, DiscoverContextCursorPageResponse>,
) : DiscoverRepository {
    override fun getDiscoverContexts(
        userId: UserId,
    ): Flow<PagingData<DiscoverContext>> {
        val pageSize = DiscoverPagingTokens.PAGE_SIZE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
            ),
            pagingSourceFactory = {
                TurninCursorPagingSource<String, DiscoverContextResponse>(
                    apiCall = { nextCursor ->
                        fetchWithCache(
                            userId = userId,
                            cursor = nextCursor,
                            pageSize = pageSize,
                        )
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(DiscoverContextResponse::toDomainModel)
            }
    }

    override fun invalidateCache(userId: UserId) {
        val firstPageCacheKey = DiscoverCacheKey(userId, null)
        val firstPageNextCursor = memoryCache[firstPageCacheKey]?.nextCursor
        memoryCache.remove(firstPageCacheKey)
        firstPageNextCursor?.let { memoryCache.remove(DiscoverCacheKey(userId, it)) }
    }

    /**
     * 캐시와 함께 페이징 데이터를 가져온다.
     *
     * 서버 요청 횟수를 줄이기 위해 초기 2페이지(Paging3 초기 로드 시 호출되는 횟수)만 캐시한다.
     * - 1페이지(cursor=null): 항상 캐시 대상
     * - 2페이지: 1페이지 응답의 nextCursor와 일치하는 경우에만 캐시 대상
     * - 3페이지 이상: 캐시하지 않음
     *
     * @param userId 조회하려는 사용자 ID
     * @param cursor 커서 값 (null이면 첫 페이지)
     * @param pageSize 페이지 사이즈
     */
    private suspend fun fetchWithCache(
        userId: UserId,
        cursor: String?,
        pageSize: Int,
    ): NetworkResult<DiscoverContextCursorPageResponse> {
        // 1페이지 캐시의 nextCursor와 현재 cursor가 일치하면 2페이지로 판단
        val isSecondPage = cursor != null &&
            cursor == memoryCache[DiscoverCacheKey(userId, null)]?.nextCursor
        val isCacheable = cursor == null || isSecondPage

        // 캐시 히트 시 네트워크 요청 없이 반환
        if (isCacheable) {
            val key = DiscoverCacheKey(userId, cursor)
            memoryCache[key]?.let { return NetworkResult.Success(it) }
        }

        // 캐시 미스 시 네트워크 요청 수행
        return discoverNetworkDataSource.getDiscoverContexts(
            userId = userId.value,
            cursor = cursor,
            size = pageSize,
        ).also { response ->
            // 캐시 대상 페이지(1~2페이지)만 저장
            if (isCacheable && response is NetworkResult.Success) {
                memoryCache[DiscoverCacheKey(userId, cursor)] = response.data
            }
        }
    }
}
