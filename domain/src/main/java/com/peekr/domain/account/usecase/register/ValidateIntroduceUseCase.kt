package com.peekr.domain.account.usecase.register

import com.peekr.domain.shared.util.CommonValidationError
import com.peekr.domain.shared.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 소개 글 유효성을 검사한다.
 */
class ValidateIntroduceUseCase @Inject constructor() {
    operator fun invoke(introduce: String): Flow<ValidationResult<CommonValidationError>> =
        flow {
            emit(ValidationResult.Loading)
            if (introduce.length in 1..INTRODUCE_MAX_LENGTH) {
                emit(ValidationResult.Success)
            } else {
                emit(ValidationResult.Error(CommonValidationError.EXCEEDS_MAX_LENGTH_200))
            }
        }
}

private const val INTRODUCE_MAX_LENGTH = 200
