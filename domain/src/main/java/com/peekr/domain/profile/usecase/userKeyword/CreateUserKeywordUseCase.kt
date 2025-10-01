package com.peekr.domain.profile.usecase.userKeyword

import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateUserKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(create: CreateUserKeyword): Flow<Result<UserKeyword, ErrorType>> =
        userKeywordRepository.createUserKeyword(create)
}
