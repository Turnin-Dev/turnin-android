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

/**
     * 예외 처리가 적용된 안전한 Result 기반 Flow를 생성합니다.
     *
     * 지정한 디스패처에서 주어진 블록을 실행하며, 예외 발생 시 선택적으로 로그를 남기고, 예외를 ErrorType으로 매핑하여 Result.Error로 내보냅니다.
     *
     * @param dispatcher Flow가 실행될 CoroutineDispatcher
     * @param enableLogging 예외 발생 시 로그 출력 여부 (기본값: true)
     * @param errorMapper 예외를 ErrorType으로 변환하는 함수 (기본값: 모든 예외를 ErrorType.Exception.Unexpected로 매핑)
     * @return 성공 시 Result.Success, 실패 시 Result.Error를 내보내는 Flow
     */
    fun <T> safeResultFlow(
    dispatcher: CoroutineDispatcher,
    enableLogging: Boolean = true,
    errorMapper: (Throwable) -> ErrorType = { ErrorType.Exception.Unexpected },
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

/**
     * 지정한 디스패처에서 실행되는 안전한 코루틴 Flow를 생성하며, 예외 발생 시 선택적으로 로깅하고 사용자 정의 에러 핸들러를 호출합니다.
     *
     * @param dispatcher Flow가 실행될 CoroutineDispatcher.
     * @param enableLogging 예외 발생 시 Timber를 통한 로깅 활성화 여부 (기본값: true).
     * @param onError 예외 발생 시 호출되는 suspend 람다 (기본값: 예외 스택 트레이스 출력).
     * @param block Flow 내에서 실행될 suspend 블록.
     * @return 예외가 안전하게 처리되는 Flow.
     */
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
