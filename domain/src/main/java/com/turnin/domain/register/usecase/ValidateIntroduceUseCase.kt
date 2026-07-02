package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.common.validation.toValidationErrorType
import com.turnin.core.domain.model.Introduce
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 소개 글 유효성을 검사한다.
 */
class ValidateIntroduceUseCase @Inject constructor() {
    operator fun invoke(introduce: String): Flow<ValidationResult<Introduce, ValidationErrorType>> = flow {
        emit(ValidationResult.Loading)
        try {
            val result = Introduce(introduce)
            emit(ValidationResult.Valid(result))
        } catch (e: CommonValidationException) {
            emit(ValidationResult.Invalid(e.toValidationErrorType()))
        }
    }
}
