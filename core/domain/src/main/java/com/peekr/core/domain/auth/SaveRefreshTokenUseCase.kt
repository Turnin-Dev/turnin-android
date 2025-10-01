package com.peekr.core.domain.auth

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Refresh Token을 DataStore에 저장한다.
 */
class SaveRefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /** @param token Refresh Token */
    operator fun invoke(token: String): Flow<Result<Boolean, ErrorType>> = flow {
        try {
            authRepository.saveRefreshToken(token)
            emit(Result.Success(true))
        } catch (e: Throwable) {
            throw e
        }
    }
}
