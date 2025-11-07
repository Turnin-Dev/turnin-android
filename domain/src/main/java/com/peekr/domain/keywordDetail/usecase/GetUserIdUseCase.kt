package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.util.Result
import com.peekr.core.domain.util.mapError
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Result<UserId, KeywordDetailErrorType>> =
        userRepository
            .getUserId()
            .mapError { userErrorType ->
                KeywordDetailErrorType.UserError(userErrorType)
            }
}
