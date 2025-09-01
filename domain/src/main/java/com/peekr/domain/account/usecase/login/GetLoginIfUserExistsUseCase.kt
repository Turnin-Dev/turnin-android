package com.peekr.domain.account.usecase.login

import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.Login
import com.peekr.domain.account.model.LoginWithExistsUser
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.common.util.flatMapResult
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
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(provider: SocialLoginProvider): Flow<Result<LoginWithExistsUser, ErrorType>> =
        socialLoginUseCase(provider)
            .flatMapResult { result: Login ->
                accountRepository
                    .existsUser(ExistsUser(result.provider, result.providerId))
                    .map { result2: Result<Boolean, ErrorType> ->
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
            .catch { e -> emit(Result.Error(ErrorType.Auth.LoginFailed)) }
}
