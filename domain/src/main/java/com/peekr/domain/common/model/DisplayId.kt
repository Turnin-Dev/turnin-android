package com.peekr.domain.common.model

import com.peekr.domain.account.rule.RegexPatterns
import com.peekr.domain.common.util.CommonValidationError
import com.peekr.domain.common.util.ValidationResult

/**
 * 사용자 표시 ID를 래핑한다.
 *
 * @property id 사용자 표시 ID (고유 ID)
 */
data class DisplayId(val id: String)

/**
 * 기능 정의서에 있는 규칙대로 사용자 표시 ID 유효성 검사를 수행 후 [ValidationResult]를 반환한다.
 */
fun DisplayId.validate(): ValidationResult<CommonValidationError> {
    val id = this.id
    return when {
        // 1) 비어 있거나 공백인 경우
        id.isEmpty() || id.isBlank() -> {
            ValidationResult.Error(CommonValidationError.EMPTY_OR_BLANK)
        }

        // 2) 길이 범위 위반 (1~30)
        id.length !in 1..DISPLAY_ID_MAX_LENGTH -> {
            ValidationResult.Error(CommonValidationError.EXCEEDS_MAX_LENGTH_30)
        }

        // 3) 허용 문자 위반
        !id.matches(RegexPatterns.displayId) -> {
            ValidationResult.Error(CommonValidationError.ONLY_ALPHANUMERIC_UNDERSCORE)
        }

        else -> ValidationResult.Success
    }
}

private const val DISPLAY_ID_MAX_LENGTH = 30
