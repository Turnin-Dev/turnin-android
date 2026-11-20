package com.turnin.core.domain.common.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다. (서버 에러 코드 포함 버전)
 *
 * 같은 공통 에러라도 서버 에러 코드([Result.Error.code])에 따라 상황이 다를 때 사용한다.
 * 변환 후에도 [Result.Error.message]와 [Result.Error.code]는 그대로 유지한다.
 *
 * @param mapper 기존 에러 타입[E]과 서버 에러 코드를 전달받아 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Result<T, E>.mapError(
    mapper: (error: E, code: ContractErrorCode) -> E2,
): Result<T, E2> = when (this) {
    Result.Loading -> Result.Loading
    is Result.Success -> this
    is Result.Error -> Result.Error(
        error = mapper(error, code),
        message = message,
        code = code,
    )
}

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다.
 *
 * 변환 후에도 [Result.Error.message]와 [Result.Error.code]는 그대로 유지한다.
 *
 * @param mapper 기존 에러 타입[E]을 파라미터로 전달하고 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Result<T, E>.mapError(
    mapper: (E) -> E2,
): Result<T, E2> = mapError { error, _ -> mapper(error) }

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다. (Flow 버전 + 서버 에러 코드 포함)
 *
 * @param mapper 기존 에러 타입[E]과 서버 에러 코드를 전달받아 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Flow<Result<T, E>>.mapError(
    mapper: (error: E, code: ContractErrorCode) -> E2,
): Flow<Result<T, E2>> = map { result -> result.mapError(mapper) }

/**
 * 서로 다른 에러 타입([E], [E2])을 매핑할 때 사용한다. (Flow 버전)
 *
 * @param mapper 기존 에러 타입[E]을 파라미터로 전달하고 변환할 에러 타입[E2]으로 변환한다.
 */
fun <T, E : BaseError, E2 : BaseError> Flow<Result<T, E>>.mapError(
    mapper: (E) -> E2,
): Flow<Result<T, E2>> = map { result -> result.mapError(mapper) }
