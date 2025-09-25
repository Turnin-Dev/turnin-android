package com.peekr.domain.account.usecase

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.account.repository.AccountRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Refresh Token을 DataStore에 저장한다.
 */
class SaveRefreshTokenUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    /** @param token Refresh Token */
    operator fun invoke(token: String): Flow<Result<Boolean, ErrorType>> = flow {
        try {
            accountRepository.saveRefreshToken(token)
            emit(Result.Success(true))
        } catch (e: Throwable) {
            throw e
        }
    }
}
