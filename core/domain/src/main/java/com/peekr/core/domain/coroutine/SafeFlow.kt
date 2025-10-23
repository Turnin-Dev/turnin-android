package com.peekr.core.domain.coroutine

import com.peekr.core.domain.util.BaseError
import com.peekr.core.domain.util.Result
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
 * @param unexpectedErrorMapper 에러 발생 시 사용할 매퍼
 * @param block flow 빌더에서 실행할 로직
 */
fun <T, E : BaseError> safeResultFlow(
    dispatcher: CoroutineDispatcher,
    unexpectedErrorMapper: (Throwable) -> E,
    block: suspend FlowCollector<Result<T, E>>.() -> Unit,
): Flow<Result<T, E>> = flow(block)
    .catch { exception ->
        if (exception is CancellationException) throw exception
        val mappedError = unexpectedErrorMapper(exception)
        emit(Result.Error(error = mappedError, message = exception.message))
    }.flowOn(dispatcher)

/**
 * 안전하게 flow 빌더를 사용할 수 있다.
 *
 * [CancellationException]예외는 대부분의 경우 직접 잡아 처리할 필요가 없고 상위 코루틴이 정상적으로 취소되게 놔둔다.
 *
 * @param dispatcher [CoroutineDispatcher]
 * @param onError 에러 발생 시
 * @param block flow 빌더에서 실행할 로직
 *
 * @throws CancellationException 코루틴이 정상적으로 취소되었을 때 코루틴 취소 예외 발생
 */
fun <T> safeFlow(
    dispatcher: CoroutineDispatcher,
    onError: suspend FlowCollector<T>.(Throwable) -> Unit = { it.printStackTrace() },
    block: suspend FlowCollector<T>.() -> Unit,
): Flow<T> = flow(block)
    .flowOn(dispatcher)
    .catch { exception ->
        if (exception is CancellationException) throw exception
        onError(exception)
    }
