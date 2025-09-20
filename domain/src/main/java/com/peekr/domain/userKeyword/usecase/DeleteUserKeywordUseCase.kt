package com.peekr.domain.userKeyword.usecase

import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
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
