package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.flatMapResult
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.eventBus.AuthEventBus
import com.peekr.domain.login.error.LoginErrorType
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

/**
 * 로그인과 동시에 리프레쉬 토큰 저장을 수행한다.
 *
 * @see flatMapResult
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginIntegrationUseCase @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus,
) {
    operator fun invoke(loginCredentials: LoginCredentials): Flow<Result<Unit, LoginErrorType>> =
        // 1. 로그인
        loginUseCase(loginCredentials)
            .flatMapConcat { loginResult ->
                return@flatMapConcat when (loginResult) {
                    Result.Loading -> flowOf(Result.Loading)
                    is Result.Error -> flowOf(Result.Error(loginResult.error))
                    is Result.Success -> {
                        // 2. 액세스 토큰 & 리프레쉬 토큰 저장
                        val accessToken = loginResult.data.accessToken
                        val refreshToken = loginResult.data.refreshToken

                        // 3. 로그인 이벤트 발행
                        authEventBus.emitLogin()

                        authRepository.saveTokens(accessToken, refreshToken)
                            .mapError { commonError ->
                                LoginErrorType.CommonError(commonError)
                            }
                    }
                }
            }
            .catch { emit(Result.Error(LoginErrorType.LoginFailed)) }
}
