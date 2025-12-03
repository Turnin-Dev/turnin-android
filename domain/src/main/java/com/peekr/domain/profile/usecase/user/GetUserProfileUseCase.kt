package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
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
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 프로필을 조회한다.
     *
     * @param userId 조회할 사용자 ID
     */
    operator fun invoke(userId: Long): Flow<Result<UserProfile, ProfileErrorType>> = flow {
        val userIdVO = UserId(userId)
        emitAll(
            combineWithResult(
                userRepository.getUserProfile(userIdVO),
                userKeywordRepository.getUserKeywords(userIdVO),
            ) { userProfile, userKeywords ->
                val myProfile = UserProfile(
                    userId = userProfile.data.userId,
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
            },
        )
    }
}
