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
