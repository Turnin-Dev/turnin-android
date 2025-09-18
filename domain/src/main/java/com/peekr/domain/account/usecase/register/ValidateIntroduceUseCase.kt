package com.peekr.domain.account.usecase.register

import com.peekr.domain.common.model.Introduce
import com.peekr.domain.common.model.IntroduceException
import com.peekr.domain.common.util.ValidationError
import com.peekr.domain.common.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 소개 글 유효성을 검사한다.
 */
class ValidateIntroduceUseCase @Inject constructor() {
    operator fun invoke(introduce: String): Flow<ValidationResult> = flow {
        emit(ValidationResult.Loading)
        try {
            Introduce(introduce)
        } catch (e: IntroduceException) {
            val validationError = when (e) {
                is IntroduceException.TooLong -> ValidationError.Introduce.TooLong(e.max)
            }
            emit(ValidationResult.Error(validationError))
        }
    }
}
