package com.peekr.domain.account.usecase

import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.SocialLoginProvider
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExistsUserUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(provider: SocialLoginProvider, providerId: String): Flow<Result<Boolean, ErrorType>> =
        accountRepository.existsUser(ExistsUser(provider, providerId))
}
