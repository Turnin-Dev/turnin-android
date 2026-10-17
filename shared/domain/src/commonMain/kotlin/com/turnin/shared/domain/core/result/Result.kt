package com.turnin.shared.domain.core.result

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 에러 베이스 타입
 *
 * 기능별 에러 타입(예: `AnnouncementErrorType`)은 이를 확장해 정의한다.
 */
interface BaseError

/**
 * 결과 래퍼 클래스 (`:core:domain`의 `Result`와 같은 구조)
 *
 * 추후 `:core:domain`의 `Result`를 이 타입으로 합칠 수 있도록 이름과 구조를 맞춘다.
 */
sealed interface Result<out T, out E : BaseError> {
    /**
     * 성공 시
     *
     * @property data 성공 후 반환할 데이터
     */
    data class Success<out T>(val data: T) : Result<T, Nothing>

    /**
     * 실패 시
     *
     * @property error 에러 타입
     * @property message 디버깅 & 로그용 메시지
     */
    data class Error<out E : BaseError>(
        val error: E,
        val message: String? = null,
    ) : Result<Nothing, E>

    data object Loading : Result<Nothing, Nothing>
}

/** 에러 타입을 변환한다. */
@OptIn(ExperimentalContracts::class)
inline fun <T, E : BaseError, R : BaseError> Result<T, E>.mapError(transform: (E) -> R): Result<T, R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Result.Success -> this
        is Result.Loading -> this
        is Result.Error -> Result.Error(error = transform(error), message = message)
    }
}

/** [Flow]로 전달되는 결과의 에러 타입을 변환한다. */
fun <T, E : BaseError, R : BaseError> Flow<Result<T, E>>.mapError(transform: (E) -> R): Flow<Result<T, R>> =
    map { result -> result.mapError(transform) }
