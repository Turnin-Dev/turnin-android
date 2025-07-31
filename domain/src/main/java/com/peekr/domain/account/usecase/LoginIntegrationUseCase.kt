package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.domain.shared.util.flatMapResult
import com.peekr.domain.shared.util.mapSuccess
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationUseCase @Inject constructor(
    private val socialLoginUseCase: SocialLoginUseCase,
    private val loginUseCase: LoginUseCase,
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase,
) {
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<Boolean, ErrorType>> =
        socialLoginUseCase(provider)
            .flatMapResult { result: Login -> loginUseCase(result) }
            .flatMapResult { result: JWTToken -> saveRefreshTokenUseCase(result.refreshToken) }
            .mapSuccess { true }
            .catch { e -> emit(Result.Error(ErrorType.Auth.LoginFailed)) }
}
