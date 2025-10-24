package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.validation.CommonValidationException
import com.peekr.core.domain.validation.ValidationErrorType
import com.peekr.core.domain.validation.ValidationResult
import com.peekr.core.domain.validation.toValidationErrorType
import javax.inject.Inject

class ValidateKeywordDescriptionUseCase @Inject constructor() {
    operator fun invoke(description: String): ValidationResult<String, ValidationErrorType> =
        try {
            val keywordDesc = KeywordDescription(description)
            ValidationResult.Valid(keywordDesc.value)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        } catch (_: Exception) {
            ValidationResult.Invalid(ValidationErrorType.Unexpected)
        }
}
