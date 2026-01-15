package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.UserProfile
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 사용자 프로필 조회
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 사용자 프로필을 조회한다.
     *
     * @param userId 조회할 사용자 ID
     */
    operator fun invoke(userId: Long): Flow<Result<UserProfile, ProfileErrorType>> = flow {
        val userIdVO = UserId(userId)
        emitAll(
            userRepository.getUserProfile(userIdVO)
                .mapSuccess { coreUserProfile ->
                    UserProfile(
                        userId = coreUserProfile.userId,
                        displayId = coreUserProfile.displayId,
                        name = coreUserProfile.name,
                        profileImageUrl = coreUserProfile.profileImageUrl,
                        introduce = coreUserProfile.introduce,
                        friendsCount = coreUserProfile.friendsCount,
                        friendStatus = coreUserProfile.friendStatus,
                        lastLoginAt = coreUserProfile.lastLoginAt,
                        active = coreUserProfile.active,
                    )
                }
                .mapError { commonError ->
                    ProfileErrorType.CommonError(commonError)
                },
        )
    }
}
