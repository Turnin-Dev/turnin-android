package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.model.Username
import com.peekr.domain.account.model.validate
import com.peekr.domain.shared.util.CommonValidationError
import com.peekr.domain.shared.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 이름 유효성을 검사한다.
 *
 * 사용자 이름은 중복이 허용되므로 중복 검사가 필요없다.
 */
class ValidateNameUseCase @Inject constructor() {
    operator fun invoke(name: String): Flow<ValidationResult<CommonValidationError>> =
        flow {
            emit(ValidationResult.Loading)
            val username = Username(name)
            val validationResult = username.validate()
            emit(validationResult)
        }
}
