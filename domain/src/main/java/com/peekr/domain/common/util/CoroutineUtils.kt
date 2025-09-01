package com.peekr.domain.common.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Coroutine의 [flatMapLatest]를 사용해서 두 개의 flow를 연속 호출하기 위해 사용한다.
 *
 * @param transform 두 개의 flow를 호출하여 [T]타입을 수신 후 [R]타입으로 반환한다.
 * ([R]타입은 Flow 타입이고 Result로 래핑되어 있다.)
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T, E : BaseError, R> Flow<Result<T, E>>.flatMapResult(
    transform: (T) -> Flow<Result<R, E>>,
): Flow<Result<R, E>> =
    flatMapLatest { result ->
        when (result) {
            Result.Loading -> flowOf(Result.Loading)
            is Result.Error -> flowOf(result)
            is Result.Success -> transform(result.data)
        }
    }

/**
 * Coroutine의 Map을 사용해서 성공 시에만 특정 값을 방출한다.
 *
 * @param transform 두 개의 flow를 호출하여 [T]타입을 수신 후 [R]타입으로 반환한다.
 * ([R]타입은 Flow 타입이고 Result로 래핑되어 있다.)
 */
inline fun <T, E : BaseError, R> Flow<Result<T, E>>.mapSuccess(
    crossinline transform: (T) -> R,
): Flow<Result<R, E>> = map { result ->
    when (result) {
        is Result.Success -> Result.Success(transform(result.data))
        is Result.Error -> result
        Result.Loading -> Result.Loading
    }
}
