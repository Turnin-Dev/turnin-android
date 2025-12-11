package com.peekr.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peekr.core.data.source.network.util.NetworkResult

/**
 * 오프셋 기반으로 동작하는 공통 PagingSource.
 *
 * @param T 응답으로 받으려는 데이터 모델
 * @param R T를 의존하고 있는 [PagingDataHolder]를 구현하는 데이터 모델
 * @param apiCall 페이지네이션 API 호출 람다 (page: 페이지 번호)
 *
 * @throws PagingApiCallException 페이징 도중 API 호출에서 에러가 발생하는 경우
 */
class PeekrPagingSource<T : Any, R : PagingDataHolder<T>>(
    private val apiCall: suspend (page: Long) -> NetworkResult<R>,
) : PagingSource<Long, T>() {
    companion object {
        const val START_PAGE_INDEX = 1L
    }

    override fun getRefreshKey(state: PagingState<Long, T>): Long? {
        // 가장 최근에 접근한 페이지를 기준으로 리프레쉬 키를 계산한다.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, T> {
        try {
            // params.key가 null이면 최초 로드이므로, START_PAGE_INDEX를 사용
            val currentPage = params.key ?: START_PAGE_INDEX

            // API 호출
            val result = apiCall(currentPage)

            return when (result) {
                is NetworkResult.Error -> {
                    LoadResult.Error(
                        PagingApiCallException(
                            error = result.error,
                            code = result.code,
                            status = result.status,
                            message = result.message,
                        ),
                    )
                }

                is NetworkResult.Success -> {
                    val pageData = result.data
                    val nextKey = if (pageData.list.isEmpty()) {
                        // 현재 페이지의 데이터가 비어있다면 다음 페이지는 없다고 판단
                        null
                    } else {
                        currentPage + 1
                    }
                    val prevKey = if (currentPage == START_PAGE_INDEX) null else currentPage - 1

                    LoadResult.Page(
                        data = pageData.list,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}
