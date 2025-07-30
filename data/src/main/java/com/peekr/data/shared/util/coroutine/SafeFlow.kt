package com.peekr.data.shared.util.coroutine

import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

fun <T> safeResultFlow(
    dispatcher: CoroutineDispatcher,
    enableLogging: Boolean = true,
    errorMapper: (Throwable) -> ErrorType = { ErrorType.Unexpected(it) },
    block: suspend FlowCollector<Result<T, ErrorType>>.() -> Unit,
): Flow<Result<T, ErrorType>> = flow(block)
    .flowOn(dispatcher)
    .catch { exception ->
        if (enableLogging) {
            Timber.e(exception, "Exception in flow: ${exception.message}")
        }
        val mappedError = errorMapper(exception)
        emit(Result.Error(error = mappedError, message = exception.message))
    }

fun <T> safeFlow(
    dispatcher: CoroutineDispatcher,
    enableLogging: Boolean = true,
    onError: suspend FlowCollector<T>.(Throwable) -> Unit = { it.printStackTrace() },
    block: suspend FlowCollector<T>.() -> Unit,
): Flow<T> = flow(block)
    .flowOn(dispatcher)
    .catch { exception ->
        if (enableLogging) {
            Timber.e(exception, "Exception in flow: ${exception.message}")
        }
        onError(exception)
    }
