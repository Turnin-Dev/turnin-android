package com.peekr.core.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다.
 *
 * @param mapper 기존 에러 타입[E]을 파라미터로 전달하고 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Result<T, E>.mapError(
    mapper: (E) -> E2,
): Result<T, E2> = when (this) {
    Result.Loading -> Result.Loading
    is Result.Success -> this
    is Result.Error -> {
        val mappedError = mapper(this.error)
        Result.Error(mappedError)
    }
}

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다. (Flow 버전)
 *
 * @param mapper 기존 에러 타입[E]을 파라미터로 전달하고 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Flow<Result<T, E>>.mapError(
    mapper: (E) -> E2,
): Flow<Result<T, E2>> = this.map { result ->
    when (result) {
        Result.Loading -> Result.Loading
        is Result.Success -> result
        is Result.Error -> {
            val mappedError = mapper(result.error)
            Result.Error(mappedError)
        }
    }
}
