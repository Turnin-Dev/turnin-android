package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.Profile
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 프로필 조회
 */
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 프로필을 조회한다.
     *
     * @param userId 조회할 사용자 ID
     */
    operator fun invoke(userId: UserId): Flow<Result<Profile, ProfileErrorType>> =
        combineWithResult(
            userRepository.getUserProfile(userId),
            userKeywordRepository.getUserKeywords(),
        ) { userProfile, userKeywords ->
            val profile = Profile(
                displayId = userProfile.data.displayId,
                name = userProfile.data.name,
                profileImageUrl = userProfile.data.profileImageUrl,
                introduce = userProfile.data.introduce,
                friendsCount = userProfile.data.friendsCount,
                lastLoginAt = userProfile.data.lastLoginAt,
                active = userProfile.data.active,
                friendshipStatus = userProfile.data.friendshipStatus,
                keywords = userKeywords.data.keywords,
            )
            Result.Success(profile)
        }.mapError { baseError ->
            when (baseError) {
                is UserErrorType -> ProfileErrorType.UserError(baseError)
                is UserKeywordErrorType -> ProfileErrorType.UserKeywordError(baseError)
                else -> ProfileErrorType.Unexpected(null)
            }
        }
}
