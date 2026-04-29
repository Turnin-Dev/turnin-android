package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.profile.model.UserProfile
import com.turnin.domain.profile.model.toUserProfile
import javax.inject.Inject

/**
 * 사용자 프로필 캐시 조회
 *
 * @see invoke
 */
class GetCachedUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 캐시에서 사용자 프로필을 조회한다.
     *
     * @param userId 사용자 ID
     */
    operator fun invoke(userId: Long): UserProfile? {
        val userIdVO = UserId(userId)
        return userRepository.getCachedUserProfile(userIdVO)?.toUserProfile()
    }
}
