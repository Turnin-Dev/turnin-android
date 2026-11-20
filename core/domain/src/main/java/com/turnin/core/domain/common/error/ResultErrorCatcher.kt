package com.turnin.core.domain.common.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * 처리되지 않은 예외를 [Result.Error]로 변환해 방출한다.
 *
 * 코루틴 취소([CancellationException])는 상위 코루틴이 정상적으로 취소되도록 그대로 전파한다.
 *
 * ```
 * // Example
 * repository.delete(id)
 *     .mapError { error -> XxxErrorType.CommonError(error) }
 *     .catchUnexpected(XxxErrorType::Unexpected)
 * ```
 *
 * @param toError 예외를 변환할 에러 타입[E]으로 변환한다.
 */
fun <T, E : BaseError> Flow<Result<T, E>>.catchUnexpected(
    toError: (Throwable) -> E,
): Flow<Result<T, E>> = catch { e ->
    if (e is CancellationException) throw e
    emit(Result.Error(error = toError(e), message = e.message))
}
