package com.peekr.domain.shared.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

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
