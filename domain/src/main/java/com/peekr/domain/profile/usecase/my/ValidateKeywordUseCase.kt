package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.KeywordValue
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
