package com.peekr.core.domain.user.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 사용자 ID 조회
 */
class GetUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Result<UserId?, CommonErrorType>> = flow {
        emit(Result.Loading)
        val userId = userRepository.getUserId()
        emit(Result.Success(userId))
    }
}
