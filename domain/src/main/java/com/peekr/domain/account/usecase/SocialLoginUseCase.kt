package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.util.AuthManagerFactory
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 소셜로그인 플랫폼에 맞게 로그인을 수행한다.
 *
 * [AuthManager]를 통해 로그인을 진행하고
 * 로그인 결과 값(사용자 ID)과 로그인을 진행한 플랫폼을 최종 반환 값([Login])으로 반환한다.
 *
 * 해당 반환 값을 [LoginUseCase]의 인자로 넣어 호출하면 로그인 과정이 마무리 된다.
 */
class SocialLoginUseCase @Inject constructor(
    private val authManagerFactory: AuthManagerFactory,
) {
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<Login, ErrorType>> {
        val authManager = authManagerFactory.create(provider)
        return authManager
            .signIn()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val login = Login(provider = provider, providerId = result.data)
                        Result.Success(login)
                    }

                    is Result.Error -> {
                        result
                    }
                }
            }.catch { e ->
                emit(Result.Error(ErrorType.Auth.Unexpected, e.message))
            }
    }
}
