package com.peekr.data.common.util.coroutine

import com.peekr.core.common.logger.AppLogger
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * 안전하게 flow 빌더를 사용할 수 있다.
 *
 * [safeFlow]와 다른 점은 반환 값으로 [Result]를 반환한다.
 *
 * [CancellationException]예외는 대부분의 경우 직접 잡아 처리할 필요가 없고 상위 코루틴이 정상적으로 취소되게 놔둔다.
 *
 * @param dispatcher [CoroutineDispatcher]
 * @param enableLogging 로깅 활성화 여부 (기본은 `true`)
 * @param errorMapper 에러 발생 시 사용할 매퍼
 * @param block flow 빌더에서 실행할 로직
 *
 * @throws CancellationException 코루틴이 정상적으로 취소되었을 때 코루틴 취소 예외 발생
 */
fun <T> safeResultFlow(
    dispatcher: CoroutineDispatcher,
    enableLogging: Boolean = true,
    errorMapper: (Throwable) -> ErrorType = { ErrorType.Unexpected(it) },
    block: suspend FlowCollector<Result<T, ErrorType>>.() -> Unit,
): Flow<Result<T, ErrorType>> = flow(block)
    .flowOn(dispatcher)
    .catch { exception ->
        if (exception is CancellationException) throw exception
        if (enableLogging) {
            AppLogger.e(TAG, exception, "Exception in flow: ${exception.message}")
        }
        val mappedError = errorMapper(exception)
        emit(Result.Error(error = mappedError, message = exception.message))
    }

/**
 * 안전하게 flow 빌더를 사용할 수 있다.
 *
 * [CancellationException]예외는 대부분의 경우 직접 잡아 처리할 필요가 없고 상위 코루틴이 정상적으로 취소되게 놔둔다.
 *
 * @param dispatcher [CoroutineDispatcher]
 * @param enableLogging 로깅 활성화 여부 (기본은 `true`)
 * @param onError 에러 발생 시
 * @param block flow 빌더에서 실행할 로직
 *
 * @throws CancellationException 코루틴이 정상적으로 취소되었을 때 코루틴 취소 예외 발생
 */
fun <T> safeFlow(
    dispatcher: CoroutineDispatcher,
    enableLogging: Boolean = true,
    onError: suspend FlowCollector<T>.(Throwable) -> Unit = { it.printStackTrace() },
    block: suspend FlowCollector<T>.() -> Unit,
): Flow<T> = flow(block)
    .flowOn(dispatcher)
    .catch { exception ->
        if (exception is CancellationException) throw exception
        if (enableLogging) {
            AppLogger.e(TAG, exception, "Exception in flow: ${exception.message}")
        }
        onError(exception)
    }

private const val TAG = "SafeFlow"
