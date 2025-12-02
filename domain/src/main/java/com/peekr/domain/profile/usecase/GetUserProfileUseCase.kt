package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.UserProfile
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
     * @param displayId 조회할 사용자 표시 ID
     */
    operator fun invoke(displayId: DisplayId): Flow<Result<UserProfile, ProfileErrorType>> =
        combineWithResult(
            userRepository.getUserProfile(displayId),
            userKeywordRepository.getUserKeywords(),
        ) { userProfile, userKeywords ->
            val myProfile = UserProfile(
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
            Result.Success(myProfile)
        }.mapError { commonError ->
            when (commonError) {
                is CommonErrorType -> ProfileErrorType.CommonError(commonError)
                else -> ProfileErrorType.Unexpected(null)
            }
        }
}
