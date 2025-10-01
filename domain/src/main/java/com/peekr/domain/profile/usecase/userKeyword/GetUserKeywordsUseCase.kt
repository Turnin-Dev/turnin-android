package com.peekr.domain.profile.usecase.userKeyword

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserKeywordsUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(userId: UserId): Flow<Result<UserKeywords, ErrorType>> =
        userKeywordRepository.getUserKeywords(userId)
}
