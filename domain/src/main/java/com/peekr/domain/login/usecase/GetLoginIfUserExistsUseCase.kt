package com.peekr.domain.login.usecase

import com.peekr.core.domain.auth.error.AuthErrorType
import com.peekr.core.domain.auth.model.ExistsUser
import com.peekr.core.domain.auth.model.Login
import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.flatMapResult
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.domain.login.model.LoginWithExistsUser
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 사용자의 존재 여부를 확인하고 사용자가 존재하면 로그인 정보를 반환한다.
 *
 * 업스트림에서 받은 [Login]을 다시 반환하고, 사용자 존재 여부를 [LoginWithExistsUser.isExistsUser]에 담아 반환한다.
 */
class GetLoginIfUserExistsUseCase @Inject constructor(
    private val socialLoginUseCase: SocialLoginUseCase,
    private val authRepository: AuthRepository,
) {
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<LoginWithExistsUser, AuthErrorType>> =
        socialLoginUseCase(provider)
            .flatMapResult { result: Login ->
                authRepository
                    .existsUser(ExistsUser(result.provider, result.providerId))
                    .map { result2: Result<Boolean, AuthErrorType> ->
                        when (result2) {
                            Result.Loading -> Result.Loading
                            is Result.Error -> result2
                            is Result.Success -> {
                                val loginWithExistsUser = LoginWithExistsUser(
                                    login = result,
                                    isExistsUser = result2.data,
                                )
                                Result.Success(loginWithExistsUser)
                            }
                        }
                    }
            }.onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(AuthErrorType.LoginFailed)) }
}
