package com.peekr.domain.account.usecase.register

import com.peekr.domain.common.model.Name
import com.peekr.domain.common.model.NameException
import com.peekr.domain.common.util.ValidationError
import com.peekr.domain.common.util.ValidationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 이름 유효성을 검사한다.
 *
 * 사용자 이름은 중복이 허용되므로 중복 검사가 필요없다.
 */
class ValidateNameUseCase @Inject constructor() {
    operator fun invoke(name: String): Flow<ValidationResult> =
        flow {
            emit(ValidationResult.Loading)
            try {
                Name(name)
            } catch (e: NameException) {
                val validationError = when (e) {
                    is NameException.Empty -> ValidationError.Name.Empty
                    is NameException.TooShortOrLong -> {
                        ValidationError.Name.TooShortOrLong(e.min, e.max)
                    }

                    is NameException.InvalidFormat -> {
                        ValidationError.Name.InvalidFormat(e.format)
                    }
                }
                emit(ValidationResult.Error(validationError))
            }
        }
}
