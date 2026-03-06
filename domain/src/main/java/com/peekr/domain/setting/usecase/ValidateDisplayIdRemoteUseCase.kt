package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.DisplayId
import com.peekr.domain.setting.error.SettingErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * 사용자 표시 ID 유효성 검사
 *
 * @see invoke
 */
class ValidateDisplayIdRemoteUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * 사용자 표시 ID 유효성 검사를 한다.
     *
     * 사용자 표시 ID는 중복검사가 필요하므로 네트워크 작업도 같이 수행한다.
     *
     * @param displayId 사용자 표시 ID
     */
    operator fun invoke(displayId: String): Flow<ValidationResult<String, SettingErrorType>> =
        flow {
            val displayId = try {
                DisplayId(displayId)
            } catch (e: CommonValidationException) {
                val validationError = SettingErrorType.ValidationError(e.toValidationErrorType())
                emit(ValidationResult.Invalid(validationError))
                return@flow
            }

            // 로컬 유효성 검사 통과 후 중복 검사 수행
            emitAll(
                authRepository.existsDisplayId(displayId)
                    .map { result ->
                        when (result) {
                            Result.Loading -> ValidationResult.Loading
                            is Result.Error -> {
                                val error = SettingErrorType.CommonError(result.error)
                                ValidationResult.Invalid(error)
                            }

                            is Result.Success -> {
                                if (result.data) {
                                    ValidationResult.Invalid(SettingErrorType.DisplayIdNotAvailable)
                                } else {
                                    ValidationResult.Valid(displayId.value)
                                }
                            }
                        }
                    }
                    .catch { emit(ValidationResult.Invalid(SettingErrorType.Unexpected(it))) },
            )
        }
}
