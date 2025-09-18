package com.peekr.domain.account.usecase.register

import com.peekr.domain.common.model.DisplayId
import com.peekr.domain.common.model.DisplayIdException
import com.peekr.domain.common.util.ValidationError
import com.peekr.domain.common.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 표시 ID 유효성을 검사한다.
 *
 * 사용자 표시 ID는 중복이 허용되지 않으므로 중복검사가 필요하다.
 */
class ValidateDisplayIdUseCase @Inject constructor() {
    operator fun invoke(displayId: String): Flow<ValidationResult> = flow {
        emit(ValidationResult.Loading)
        try {
            DisplayId(displayId)
            emit(ValidationResult.Success)
        } catch (e: DisplayIdException) {
            val validationError = when (e) {
                is DisplayIdException.Empty -> ValidationError.DisplayId.Empty
                is DisplayIdException.TooShortOrLong -> {
                    ValidationError.DisplayId.TooShortOrLong(e.min, e.max)
                }

                is DisplayIdException.InvalidFormat -> {
                    ValidationError.DisplayId.InvalidFormat(e.format)
                }
            }
            emit(ValidationResult.Error(validationError))
        }
    }
}
