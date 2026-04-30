package com.turnin.domain.register.usecase

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.DisplayId
import com.turnin.domain.register.error.RegisterErrorType
import com.turnin.domain.register.model.ExistsResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * 사용자 표시 ID 중복검사
 */
class CheckDisplayIdExistsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(displayId: String): Flow<Result<ExistsResult, RegisterErrorType>> =
        authRepository
            .existsDisplayId(DisplayId(displayId))
            .distinctUntilChanged()
            .map { result ->
                when (result) {
                    Result.Loading -> Result.Loading
                    is Result.Error -> result
                    is Result.Success -> Result.Success(ExistsResult(result.data))
                }
            }.mapError { RegisterErrorType.CommonError(it) }
}
