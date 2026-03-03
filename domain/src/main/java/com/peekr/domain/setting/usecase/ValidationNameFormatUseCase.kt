package com.peekr.domain.setting.usecase

import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.Name
import javax.inject.Inject

/**
 * 사용자 명 유효성 검사
 *
 * @see invoke
 */
class ValidationNameFormatUseCase @Inject constructor() {
    /**
     * 사용자 명 유효성 검사를 한다.
     *
     * @param name 사용자 명
     */
    operator fun invoke(name: String): ValidationResult<Name, ValidationErrorType> =
        try {
            val result = Name(name)
            ValidationResult.Valid(result)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        }
}
