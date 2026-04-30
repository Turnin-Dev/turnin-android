package com.turnin.core.domain.user.usecase

import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.util.DomainLogger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

/**
 * 나의 사용자 ID 조회
 */
class GetMyUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val logger: DomainLogger,
) {
    private val tag = this::class.java.simpleName

    suspend operator fun invoke(): UserId? =
        try {
            userRepository.getMyUserId()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            logger.e(tag, e, "Error getting my user ID")
            null
        }
}
