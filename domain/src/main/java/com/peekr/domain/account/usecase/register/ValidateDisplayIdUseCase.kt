package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.DisplayId
import com.peekr.domain.account.model.validate
import com.peekr.domain.shared.util.CommonValidationError
import com.peekr.domain.shared.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 표시 ID 유효성을 검사한다.
 *
 * 사용자 표시 ID는 중복이 허용되지 않으므로 중복검사가 필요하다.
 */
class ValidateDisplayIdUseCase @Inject constructor() {
    operator fun invoke(displayId: String): Flow<ValidationResult<CommonValidationError>> = flow {
        emit(ValidationResult.Loading)
        val id = DisplayId(displayId)
        val validationResult = id.validate()
        emit(validationResult)
    }
}
