package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.CommonErrorType
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.MyProfile
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 프로필 조회
 */
class GetMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 프로필을 조회한다.
     */
    operator fun invoke(): Flow<Result<MyProfile, ProfileErrorType>> =
        combineWithResult(
            userRepository.getMyProfile(),
            userKeywordRepository.getUserKeywords(),
        ) { myProfile, userKeywords ->
            val profile = MyProfile(
                displayId = myProfile.data.displayId,
                name = myProfile.data.name,
                profileImageUrl = myProfile.data.profileImageUrl,
                introduce = myProfile.data.introduce,
                friendsCount = myProfile.data.friendsCount,
                lastLoginAt = myProfile.data.lastLoginAt,
                active = myProfile.data.active,
                keywords = userKeywords.data.keywords,
            )
            Result.Success(profile)
        }.mapError { commonError ->
            when (commonError) {
                is CommonErrorType -> ProfileErrorType.CommonError(commonError)
                else -> ProfileErrorType.Unexpected(null)
            }
        }
}
