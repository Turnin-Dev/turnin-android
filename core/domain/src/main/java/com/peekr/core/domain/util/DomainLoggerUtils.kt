package com.peekr.core.domain.util

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch

/**
 * Flow의 예외 처리와 로깅을 한 번에 수행한다.
 *
 * @param logger 도메인 로거 [DomainLogger]
 * @param tag 로그 태그
 * @param action
 *
 * @see catch
 */
fun <T, E : BaseError> Flow<Result<T, E>>.catchAndLog(
    logger: DomainLogger,
    tag: String,
    action: suspend FlowCollector<Result<T, E>>.(Throwable) -> Unit,
): Flow<Result<T, E>> = this.catch { e ->
    logger.e(tag, e, e.message ?: "[CatchAndLog] Unexpected Error occurred.")
    action(e)
}
