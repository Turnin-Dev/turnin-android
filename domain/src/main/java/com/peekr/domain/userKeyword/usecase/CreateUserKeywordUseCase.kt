package com.peekr.domain.userKeyword.usecase

import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.userKeyword.model.CreateUserKeyword
import com.peekr.domain.userKeyword.model.UserKeyword
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateUserKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(create: CreateUserKeyword): Flow<Result<UserKeyword, ErrorType>> =
        userKeywordRepository.createUserKeyword(create)
}
