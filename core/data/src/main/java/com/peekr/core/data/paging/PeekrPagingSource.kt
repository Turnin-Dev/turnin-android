package com.peekr.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.error.CommonErrorType
import kotlinx.coroutines.CancellationException

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
    private val tag = this::class.java.simpleName

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
                    AppLogger.d(tag, "Paging source load failed: ${result.error.toCommonErrorType()}")
                    LoadResult.Error(
                        PagingApiCallException(
                            error = result.error.toCommonErrorType(),
                            message = result.error.toErrorMessage(),
                        ),
                    )
                }

                is NetworkResult.Success -> {
                    val pageData = result.data
                    val nextKey = if (pageData.hasNext) {
                        currentPage + 1
                    } else {
                        null
                    }
                    val prevKey = if (currentPage == START_PAGE_INDEX) null else currentPage - 1

                    AppLogger.d(tag, "Paging source load successful.")
                    LoadResult.Page(
                        data = pageData.list,
                        prevKey = prevKey,
                        nextKey = nextKey,
                    )
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            AppLogger.e(tag, e, "Paging source exception during load.")
            return LoadResult.Error(
                PagingApiCallException(
                    error = CommonErrorType.Unexpected(e),
                    message = UNEXPECTED_MESSAGE,
                ),
            )
        }
    }
}

private fun NetworkErrorType.toErrorMessage(): String = when (this) {
    NetworkErrorType.Exception.IO -> "네트워크 연결을 확인해주세요."
    NetworkErrorType.Exception.TimeOut -> "네트워크 연결 시간이 초과되었어요."
    NetworkErrorType.Exception.JsonData -> "서버 통신 과정에서 오류가 발생했어요."
    NetworkErrorType.Exception.JsonEncoding -> "서버 통신 과정에서 오류가 발생했어요."
    NetworkErrorType.Exception.MalformedJson -> "서버 통신 과정에서 오류가 발생했어요."
    is NetworkErrorType.Network.HttpError -> "서버 통신 과정에서 오류가 발생했어요."
    NetworkErrorType.Network.ConnectionFailed -> "네트워크 연결을 확인해주세요."
    else -> UNEXPECTED_MESSAGE
}

private const val UNEXPECTED_MESSAGE = "잠시 오류가 발생했어요. 다시 시도 해주세요."
