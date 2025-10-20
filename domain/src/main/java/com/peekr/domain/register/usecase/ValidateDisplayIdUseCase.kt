package com.peekr.domain.register.usecase

import com.peekr.core.domain.user.model.DisplayId
import com.peekr.core.domain.validation.CommonValidationException
import com.peekr.core.domain.validation.ValidationResult
import com.peekr.core.domain.validation.toValidationError
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 표시 ID 유효성을 검사한다.
 *
 * 사용자 표시 ID는 중복이 허용되지 않으므로 중복검사가 필요하다.
 */
class ValidateDisplayIdUseCase @Inject constructor() {
    operator fun invoke(displayId: String): Flow<ValidationResult<DisplayId>> = flow {
        emit(ValidationResult.Loading)
        try {
            val result = DisplayId(displayId)
            emit(ValidationResult.Valid(result))
        } catch (e: CommonValidationException) {
            emit(ValidationResult.Invalid(e.toValidationError()))
        }
    }
}
