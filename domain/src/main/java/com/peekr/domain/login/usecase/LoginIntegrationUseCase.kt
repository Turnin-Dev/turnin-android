package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.SaveRefreshTokenUseCase
import com.peekr.core.domain.auth.error.AuthErrorType
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.flatMapResult
import com.peekr.core.domain.common.coroutine.mapSuccess
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

/**
 * 로그인과 동시에 리프레쉬 토큰 저장을 수행한다.
 *
 * @see flatMapResult
 * @see mapSuccess
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationUseCase @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveRefreshTokenUseCase: SaveRefreshTokenUseCase,
) {
    operator fun invoke(login: Login): Flow<Result<Boolean, AuthErrorType>> =
        loginUseCase(login)
            .flatMapResult { result: LoginResult -> saveRefreshTokenUseCase(result.refreshToken) }
            .mapSuccess { true }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(AuthErrorType.LoginFailed)) }
}
