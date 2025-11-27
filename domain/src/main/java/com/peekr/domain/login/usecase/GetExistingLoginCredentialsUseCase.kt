package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
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
 * 이미 존재하는 사용자의 로그인 정보 조회
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetExistingLoginCredentialsUseCase @Inject constructor(
    private val socialLoginUseCase: SocialLoginUseCase,
    private val authRepository: AuthRepository,
) {
    /**
     * 사용자의 존재 여부를 확인하고 사용자가 존재하면 로그인 정보를 반환한다.
     *
     * 업스트림에서 받은 [LoginCredentials]을 다시 반환하고, 사용자 존재 여부를 [LoginWithExistsUser.isExistsUser]에 담아 반환한다.
     */
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<LoginWithExistsUser, LoginErrorType>> =
        // 1. 소셜 로그인
        socialLoginUseCase(provider).flatMapConcat { socialLoginResult ->
            return@flatMapConcat when (socialLoginResult) {
                Result.Loading -> flowOf(Result.Loading)
                is Result.Error -> flowOf(Result.Error(socialLoginResult.error))
                is Result.Success -> {
                    // 2. 사용자 존재 여부 확인 후 로그인 결과 반환
                    val existsUser = ExistsUser(
                        provider = socialLoginResult.data.provider,
                        providerId = socialLoginResult.data.providerId,
                    )
                    authRepository.existsUser(existsUser).map { userResult ->
                        when (userResult) {
                            Result.Loading -> Result.Loading
                            is Result.Error -> Result.Error(LoginErrorType.CommonError(userResult.error))
                            is Result.Success -> {
                                val loginWithExistsUser = LoginWithExistsUser(
                                    loginCredentials = socialLoginResult.data,
                                    isExistsUser = userResult.data,
                                )
                                Result.Success(loginWithExistsUser)
                            }
                        }
                    }
                }
            }
        }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(LoginErrorType.LoginFailed)) }
}
