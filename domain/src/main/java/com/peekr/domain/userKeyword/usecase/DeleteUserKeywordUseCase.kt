package com.peekr.domain.userKeyword.usecase

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DeleteUserKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<Unit, ErrorType>> =
        userKeywordRepository.deleteUserKeyword(userId, userKeywordId)
}
