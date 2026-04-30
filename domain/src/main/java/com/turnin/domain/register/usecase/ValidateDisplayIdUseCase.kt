package com.turnin.domain.register.usecase

import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.common.validation.ValidationErrorType
import com.turnin.core.domain.common.validation.ValidationResult
import com.turnin.core.domain.common.validation.toValidationErrorType
import com.turnin.core.domain.model.DisplayId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 표시 ID 유효성을 검사한다.
 *
 * 사용자 표시 ID는 중복이 허용되지 않으므로 중복검사가 필요하다.
 */
class ValidateDisplayIdUseCase @Inject constructor() {
    operator fun invoke(displayId: String): Flow<ValidationResult<DisplayId, ValidationErrorType>> =
        flow {
            emit(ValidationResult.Loading)
            try {
                val result = DisplayId(displayId)
                emit(ValidationResult.Valid(result))
            } catch (e: CommonValidationException) {
                emit(ValidationResult.Invalid(e.toValidationErrorType()))
            }
        }
}
