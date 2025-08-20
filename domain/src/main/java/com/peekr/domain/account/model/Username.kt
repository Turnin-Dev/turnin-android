package com.peekr.domain.account.model

import com.peekr.domain.account.rule.RegexPatterns
import com.peekr.domain.shared.util.CommonValidationError
import com.peekr.domain.shared.util.ValidationResult

/** 사용자 이름 */
data class Username(val name: String)

fun Username.validate(): ValidationResult<CommonValidationError> {
    val name = this.name
    return when {
        // 1) 비어 있거나 공백인 경우
        name.isEmpty() || name.isBlank() -> {
            ValidationResult.Error(CommonValidationError.EMPTY_OR_BLANK)
        }

        // 2) 길이 범위 위반 (1~30)
        name.length !in 1..NAME_MAX_LENGTH -> {
            ValidationResult.Error(CommonValidationError.EXCEEDS_MAX_LENGTH)
        }

        // 3) 허용 문자 위반
        !name.matches(RegexPatterns.name) -> {
            ValidationResult.Error(CommonValidationError.ONLY_ALPHANUMERIC_HANGUL)
        }

        else -> ValidationResult.Success
    }
}

private const val NAME_MAX_LENGTH = 30
