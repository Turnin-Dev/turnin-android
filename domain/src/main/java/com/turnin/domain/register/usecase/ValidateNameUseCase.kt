package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.common.validation.toValidationErrorType
import com.turnin.core.domain.model.Name
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 이름 유효성을 검사한다.
 *
 * 사용자 이름은 중복이 허용되므로 중복 검사가 필요없다.
 */
class ValidateNameUseCase @Inject constructor() {
    operator fun invoke(name: String): Flow<ValidationResult<Name, ValidationErrorType>> = flow {
        emit(ValidationResult.Loading)
        try {
            val result = Name(name)
            emit(ValidationResult.Valid(result))
        } catch (e: CommonValidationException) {
            emit(ValidationResult.Invalid(e.toValidationErrorType()))
        }
    }
}
