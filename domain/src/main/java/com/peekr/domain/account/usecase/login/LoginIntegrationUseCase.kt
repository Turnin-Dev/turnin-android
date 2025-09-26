package com.peekr.domain.account.usecase.login

import com.peekr.core.domain.coroutine.flatMapResult
import com.peekr.core.domain.coroutine.mapSuccess
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.account.model.JWTToken
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.usecase.SaveRefreshTokenUseCase
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
    operator fun invoke(login: Login): Flow<Result<Boolean, ErrorType>> =
        loginUseCase(login)
            .flatMapResult { result: JWTToken -> saveRefreshTokenUseCase(result.refreshToken) }
            .mapSuccess { true }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(ErrorType.Auth.LoginFailed)) }
}
