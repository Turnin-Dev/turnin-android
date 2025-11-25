package com.peekr.core.domain.common.coroutine

import com.peekr.core.domain.common.BaseError
import com.peekr.core.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * [combine] 확장 버전으로, Result 래퍼 클래스와 함께 사용한다.
 *
 * 이 확장함수를 사용할 때 [transform] 파라미터에 [Result.Success] 데이터만 넣어주면 된다.
 *
 * @param transform 두 개의 수신 타입([T1], [T2])을 [R] 타입으로 변환해서 반환한다.
 */
fun <T1, T2, R> combineWithResult(
    flow1: Flow<Result<T1, BaseError>>,
    flow2: Flow<Result<T2, BaseError>>,
    transform: suspend (a: Result.Success<T1>, b: Result.Success<T2>) -> Result.Success<R>,
): Flow<Result<R, BaseError>> = combine(flow1, flow2) { f1, f2 ->
    when {
        f1 is Result.Loading || f2 is Result.Loading -> Result.Loading
        f1 is Result.Error -> f1
        f2 is Result.Error -> f2
        else -> {
            f1 as Result.Success
            f2 as Result.Success
            transform(f1, f2)
        }
    }
}

/**
 * [combine] 확장 버전으로, Result 래퍼 클래스와 함께 사용한다.
 *
 * 이 확장함수를 사용할 때 [transform] 파라미터에 [Result.Success] 데이터만 넣어주면 된다.
 */
fun <T1, T2, T3, R> combineWithResult(
    flow1: Flow<Result<T1, BaseError>>,
    flow2: Flow<Result<T2, BaseError>>,
    flow3: Flow<Result<T3, BaseError>>,
    transform: suspend (
        a: Result.Success<T1>,
        b: Result.Success<T2>,
        c: Result.Success<T3>,
    ) -> Result.Success<R>,
): Flow<Result<R, BaseError>> = combine(flow1, flow2, flow3) { f1, f2, f3 ->
    when {
        f1 is Result.Loading || f2 is Result.Loading || f3 is Result.Loading -> Result.Loading
        f1 is Result.Error -> f1
        f2 is Result.Error -> f2
        f3 is Result.Error -> f3
        else -> {
            f1 as Result.Success
            f2 as Result.Success
            f3 as Result.Success
            transform(f1, f2, f3)
        }
    }
}
