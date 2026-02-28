package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.login.error.LoginErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * 소셜로그인
 */
class SocialLoginUseCase @Inject constructor(
    private val socialAuthManagerFactory: SocialAuthManagerFactory,
) {
    /**
     * 소셜로그인 플랫폼에 맞게 로그인을 수행한다.
     *
     * `AuthManager` 를 통해 로그인을 진행하고 로그인 결과 값(사용자 ID)과
     * 로그인을 진행한 플랫폼을 [LoginCredentials] 객체로 반환한다.
     *
     * 반환된 [LoginCredentials] 객체는 [LoginUseCase]에 전달하여 최종 로그인 과정을 마무리한다.
     *
     * @param provider 로그인에 사용할 소셜 플랫폼
     *
     * @return [Result] – 성공 시 [LoginCredentials], 실패 시 [LoginErrorType] 정보 포함
     */
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<LoginCredentials, LoginErrorType>> {
        val authManager = socialAuthManagerFactory.create(provider)
        return authManager
            .signIn()
            .mapSuccess { providerId ->
                LoginCredentials(provider = provider, providerId = providerId)
            }
            .mapError { commonError ->
                LoginErrorType.CommonError(commonError) as LoginErrorType
            }
            .catch { e ->
                emit(Result.Error(error = LoginErrorType.Unexpected(e), message = e.message))
            }
    }
}
