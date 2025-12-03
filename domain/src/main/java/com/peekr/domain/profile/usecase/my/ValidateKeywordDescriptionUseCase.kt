package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.KeywordDescription
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
