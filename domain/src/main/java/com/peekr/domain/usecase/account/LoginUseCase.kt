package com.peekr.domain.usecase.account

import com.peekr.domain.model.account.Login
import com.peekr.domain.repository.AccountRepository
import com.peekr.domain.util.ErrorType
import com.peekr.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// TODO: 의존성 주입 필요
class LoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke(login: Login): Flow<Result<Boolean, ErrorType>> =
        accountRepository.login(login)
}
