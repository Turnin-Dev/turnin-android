package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.login.error.LoginErrorType
import com.peekr.domain.login.model.LoginWithExistsUser
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 소셜 로그인 후 사용자 존재 여부를 확인하고 로그인 정보를 반환한다.
 *
 * 업스트림에서 받은 [LoginCredentials]을 다시 반환하고, 사용자 존재 여부를 [LoginWithExistsUser.isExistsUser]에 담아 반환한다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetSocialLoginResultUseCase @Inject constructor(
    private val socialAuthManagerFactory: SocialAuthManagerFactory,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<LoginWithExistsUser, LoginErrorType>> {
        val authManager = socialAuthManagerFactory.create(provider)
        // 1. 소셜로그인
        return authManager
            .signIn()
            .mapSuccess { providerId ->
                LoginCredentials(provider = provider, providerId = providerId)
            }
            .mapError { commonError ->
                LoginErrorType.CommonError(commonError) as LoginErrorType
            }
            .flatMapConcat { socialLoginResult ->
                when (socialLoginResult) {
                    Result.Loading -> flowOf(Result.Loading)
                    is Result.Error -> flowOf(Result.Error(socialLoginResult.error))
                    is Result.Success -> {
                        // 2. 사용자 존재 여부 확인 후 로그인 결과 반환
                        val existsUser = ExistsUser(
                            provider = socialLoginResult.data.provider,
                            providerId = socialLoginResult.data.providerId,
                        )
                        authRepository.existsUser(existsUser).map { isExistsUserResult ->
                            when (isExistsUserResult) {
                                Result.Loading -> Result.Loading
                                is Result.Error -> Result.Error(LoginErrorType.CommonError(isExistsUserResult.error))
                                is Result.Success -> Result.Success(
                                    LoginWithExistsUser(
                                        loginCredentials = socialLoginResult.data,
                                        isExistsUser = isExistsUserResult.data,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
            .onStart { emit(Result.Loading) }
            .catch { e ->
                emit(Result.Error(error = LoginErrorType.LoginFailed, message = e.message))
            }
    }
}
