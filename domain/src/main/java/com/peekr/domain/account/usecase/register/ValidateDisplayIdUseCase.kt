package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.rule.RegexPatterns
import com.peekr.domain.shared.util.validation.ValidationError
import com.peekr.domain.shared.util.validation.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 표시 ID 유효성을 검사한다.
 *
 * 사용자 표시 ID는 사용자 이름과 다르게 고유한 값이므로 서버에서 중복검사가 필요하다.
 */
class ValidateDisplayIdUseCase {
    operator fun invoke(displayId: String): Flow<ValidationResult> = flow {
        emit(ValidationResult.Loading)
        // 1. 기능 정의서에 있는 규칙 검사
        when {
            displayId.length !in 1..30 -> {
                emit(ValidationResult.Error(ValidationError.EXCEEDS_MAX_LENGTH))
                return@flow
            }

            displayId.matches(RegexPatterns.displayId) == false -> {
                emit(ValidationResult.Error(ValidationError.ONLY_ALPHANUMERIC_UNDERSCORE))
                return@flow
            }
        }
        // 2. 서버에서 중복검사

        emit(ValidationResult.Success)
    }
}
