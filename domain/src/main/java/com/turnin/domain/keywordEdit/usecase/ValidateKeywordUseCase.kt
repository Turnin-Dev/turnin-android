package com.turnin.domain.keywordEdit.usecase

import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.common.validation.toValidationErrorType
import com.turnin.core.domain.model.KeywordName
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/** 키워드 유효성 검사 */
class ValidateKeywordUseCase @Inject constructor() {
    operator fun invoke(keyword: String): ValidationResult<String, ValidationErrorType> =
        try {
            val keyword = KeywordName(keyword)
            ValidationResult.Valid(keyword.value)
        } catch (e: CommonValidationException) {
            ValidationResult.Invalid(e.toValidationErrorType())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ValidationResult.Invalid(ValidationErrorType.Unexpected)
        }
}
