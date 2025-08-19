package com.peekr.domain.account.model

import com.peekr.domain.account.rule.RegexPatterns
import com.peekr.domain.account.validation.RegisterValidationError
import com.peekr.domain.shared.util.ValidationResult

/**
 * 사용자 표시 ID를 래핑한다.
 *
 * @property id 사용자 표시 ID (고유 ID)
 */
data class DisplayId(val id: String)

/**
 * 기능 정의서에 있는 규칙대로 사용자 표시 ID 유효성 검사를 수행 후 [ValidationResult]를 반환한다.
 */
fun DisplayId.validate(): ValidationResult<RegisterValidationError> {
    val id = this.id
    return when {
        id.isEmpty() || id.isBlank() -> {
            ValidationResult.Error(RegisterValidationError.EMPTY_OR_BLACK)
        }

        id.length !in 1..30 -> {
            ValidationResult.Error(RegisterValidationError.EXCEEDS_MAX_LENGTH)
        }

        id.matches(RegexPatterns.displayId) == false -> {
            ValidationResult.Error(RegisterValidationError.ONLY_ALPHANUMERIC_UNDERSCORE)
        }

        else -> ValidationResult.Success
    }
}
