package com.peekr.domain.register.usecase

import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.IntroduceException
import com.peekr.core.domain.model.toValidationError
import com.peekr.core.domain.validation.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 소개 글 유효성을 검사한다.
 */
class ValidateIntroduceUseCase @Inject constructor() {
    operator fun invoke(introduce: String): Flow<ValidationResult<Introduce>> = flow {
        emit(ValidationResult.Loading)
        try {
            val result = Introduce(introduce)
            emit(ValidationResult.Valid(result))
        } catch (e: IntroduceException) {
            emit(ValidationResult.Invalid(e.toValidationError()))
        }
    }
}
