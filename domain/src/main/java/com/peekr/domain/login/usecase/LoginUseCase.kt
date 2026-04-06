package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
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
 * 로그인과 토큰 저장을 통합 수행하는 유스케이스.
 *
 * 아래 세 단계를 순차적으로 수행한다:
 * 1. [AuthRepository.login]을 통해 서버 로그인 수행
 * 2. 발급된 액세스 토큰 & 리프레시 토큰 저장
 * 3. 로그인 이벤트 발행 ([AuthEventBus.emitLogin])
 *
 * 각 단계에서 발생한 에러는 [LoginErrorType.CommonError]로 래핑되어 반환된다.
 * 예외 발생 시 [LoginErrorType.LoginFailed]로 반환된다.
 *
 * @see invoke
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authEventBus: AuthEventBus,
) {
    /**
     * @param loginCredentials 플랫폼·사용자 식별 정보를 담은 객체
     * @return [Result] – 성공 시 [Unit], 실패 시 [LoginErrorType]
     */
    operator fun invoke(loginCredentials: LoginCredentials): Flow<Result<Unit, LoginErrorType>> =
        // 1. 서버 로그인 수행
        authRepository
            .login(loginCredentials)
            .mapError { commonError ->
                LoginErrorType.CommonError(commonError) as LoginErrorType
            }
            .flatMapConcat { loginResult ->
                when (loginResult) {
                    Result.Loading -> flowOf(Result.Loading)
                    is Result.Error -> flowOf(Result.Error(loginResult.error))
                    is Result.Success -> {
                        // 2. 액세스 토큰 & 리프레쉬 토큰 저장
                        val accessToken = loginResult.data.accessToken
                        val refreshToken = loginResult.data.refreshToken

                        authRepository.saveTokens(accessToken, refreshToken)
                            .mapSuccess {
                                // 3. 로그인 이벤트 발행
                                authEventBus.emitLogin()
                            }
                            .mapError { commonError ->
                                LoginErrorType.CommonError(commonError)
                            }
                    }
                }
            }
            .catch { e ->
                emit(Result.Error(LoginErrorType.LoginFailed, e.message))
            }
}
