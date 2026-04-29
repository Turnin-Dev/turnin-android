package com.turnin.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.CursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.PagingApiCallException
import kotlinx.coroutines.CancellationException

/**
 * 커서 기반으로 동작하는 공통 PagingSource.
 *
 * @param K 커서 타입
 * @param T 응답으로 받으려는 데이터 모델 ([CursorPageResponse]에서 T에 해당된다.)
 * @param apiCall 페이지네이션 API 호출 람다 (cursor: 다음 커서 값)
 *
 * @throws com.turnin.core.domain.common.error.PagingApiCallException 페이징 도중 API 호출에서 에러가 발생하는 경우
 */
class PeekrCursorPagingSource<K : Any, T : Any>(
    private val apiCall: suspend (cursor: K?) -> NetworkResult<CursorPageResponse<out T, out K>>,
) : PagingSource<K, T>() {
    private val tag = this::class.java.simpleName

    override fun getRefreshKey(state: PagingState<K, T>): K? = null

    override suspend fun load(params: LoadParams<K>): LoadResult<K, T> {
        try {
            // params.key가 null이면 최초 로드
            val currentCursor = params.key

            // API 호출
            val result = apiCall(currentCursor)

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

                    AppLogger.d(tag, "Paging source load successful.")

                    LoadResult.Page(
                        data = pageData.items,
                        prevKey = null,
                        nextKey = pageData.nextCursor,
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
