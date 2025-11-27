package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.model.LoginResult
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.domain.login.error.LoginErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * [LoginCredentials] 정보를 이용해 로그인 기능을 수행한다.
 *
 * 일반적으로 [SocialLoginUseCase]의 반환값을 그대로 넘겨 호출한다.
 *
 * 로그인 성공 시 발급된 [LoginResult]을 반환한다.
 * 토큰 저장은 상위 계층(예: [LoginIntegrationUseCase])에서 처리한다.
 *
 * @return [Result] – 성공 시 [LoginResult], 실패 시 [LoginErrorType]
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /** @param loginCredentials 플랫폼·사용자 식별 정보를 담은 객체 */
    operator fun invoke(loginCredentials: LoginCredentials): Flow<Result<LoginResult, LoginErrorType>> =
        authRepository
            .login(loginCredentials)
            .mapError { commonError ->
                LoginErrorType.CommonError(commonError)
            }
}
