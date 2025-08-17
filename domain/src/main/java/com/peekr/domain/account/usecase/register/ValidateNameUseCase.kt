package com.peekr.domain.account.usecase.register

import com.peekr.domain.account.rule.RegexPatterns
import com.peekr.domain.shared.util.validation.ValidationError
import com.peekr.domain.shared.util.validation.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 이름 유효성을 검사한다.
 *
 * 사용자 이름은 중복이 허용되므로 중복 검사가 필요없다.
 */
class ValidateNameUseCase {
    operator fun invoke(displayId: String): Flow<ValidationResult> = flow {
        emit(ValidationResult.Loading)

        when {
            displayId.length !in 1..30 -> {
                emit(ValidationResult.Error(ValidationError.ExceedsLength))
            }

            displayId.matches(RegexPatterns.username) == false -> {
                emit(ValidationResult.Error(ValidationError.RequireEnglishNumberHangeul))
            }
        }

        emit(ValidationResult.Success)
    }
}
