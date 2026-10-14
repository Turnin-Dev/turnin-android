package com.turnin.domain.setting.usecase

import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.common.validation.toValidationErrorType
import com.turnin.core.domain.model.Name

/**
 * 사용자 명 유효성 검사
 *
 * @see invoke
 */
class ValidationNameFormatUseCase {
    /**
     * 사용자 명 유효성 검사를 한다.
     *
     * @param name 사용자 명
     */
    operator fun invoke(name: String): ValidationResult<String, ValidationErrorType> =
        try {
            val result = Name(name)
            ValidationResult.Valid(result.value)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        }
}
