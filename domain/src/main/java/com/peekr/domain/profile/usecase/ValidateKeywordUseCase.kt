package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.validation.CommonValidationException
import com.peekr.core.domain.validation.ValidationErrorType
import com.peekr.core.domain.validation.ValidationResult
import com.peekr.core.domain.validation.toValidationErrorType
import javax.inject.Inject

/** 키워드 유효성 검사 */
class ValidateKeywordUseCase @Inject constructor() {
    operator fun invoke(keyword: String): ValidationResult<String, ValidationErrorType> =
        try {
            val keyword = KeywordValue(keyword)
            ValidationResult.Valid(keyword.value)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        } catch (_: Exception) {
            ValidationResult.Invalid(ValidationErrorType.Unexpected)
        }
}
