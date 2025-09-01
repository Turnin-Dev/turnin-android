package com.peekr.domain.account.usecase.login

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.util.AuthManagerFactory
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.common.util.Result.Success
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 소셜로그인 플랫폼에 맞게 로그인을 수행한다.
 *
 * `AuthManager` 를 통해 로그인을 진행하고 로그인 결과 값(사용자 ID)과
 * 로그인을 진행한 플랫폼을 [Login] 객체로 반환한다.
 *
 * 반환된 [Login] 객체는 [LoginUseCase]에 전달하여 최종 로그인 과정을 마무리한다.
 *
 * @return [Result] – 성공 시 [Login], 실패 시 [ErrorType] 정보 포함
 */
class SocialLoginUseCase @Inject constructor(
    private val authManagerFactory: AuthManagerFactory,
) {
    /** @param provider 로그인에 사용할 소셜 플랫폼 */
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<Login, ErrorType>> {
        val authManager = authManagerFactory.create(provider)
        return authManager
            .signIn()
            .map { result ->
                when (result) {
                    Result.Loading -> Result.Loading
                    is Success -> {
                        val login = Login(provider = provider, providerId = result.data)
                        Success(login)
                    }

                    is Result.Error -> result
                }
            }.catch { e ->
                emit(Result.Error(error = ErrorType.Unexpected(e), message = e.message))
            }
    }
}
