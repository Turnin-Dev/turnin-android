package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.Login
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// TODO: 의존성 주입 필요

/**
 * [Login]을 통해 로그인 기능을 수행한다.
 *
 * [SocialLoginUseCase]의 반환 값인 [Login]을 인자로 넣어 호출한다.
 */
class LoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke(login: Login): Flow<Result<Boolean, ErrorType>> =
        accountRepository.login(login)
}
