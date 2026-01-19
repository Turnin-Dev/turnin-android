package com.peekr.core.domain.user.usecase

import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import javax.inject.Inject

/**
 * 사용자 ID 조회
 */
class GetUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): UserId? =
        userRepository.getUserId()
}
