package com.peekr.domain.setting.usecase

import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.Introduce
import javax.inject.Inject

/**
 * 소개글 유효성 검사
 *
 * @see invoke
 */
class ValidationIntroduceFormatUseCase @Inject constructor() {
    /**
     * 소개글 유효성 검사를 한다.
     *
     * @param introduce 소개글
     */
    operator fun invoke(introduce: String): ValidationResult<Introduce, ValidationErrorType> =
        try {
            val result = Introduce(introduce)
            ValidationResult.Valid(result)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        }
}
