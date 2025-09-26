package com.peekr.domain.account.usecase.register

import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.NameException
import com.peekr.core.domain.model.toValidationError
import com.peekr.core.domain.validation.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 이름 유효성을 검사한다.
 *
 * 사용자 이름은 중복이 허용되므로 중복 검사가 필요없다.
 */
class ValidateNameUseCase @Inject constructor() {
    operator fun invoke(name: String): Flow<ValidationResult<Name>> = flow {
        emit(ValidationResult.Loading)
        try {
            val result = Name(name)
            emit(ValidationResult.Valid(result))
        } catch (e: NameException) {
            emit(ValidationResult.Invalid(e.toValidationError()))
        }
    }
}
