package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.mapSuccess
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.profile.error.ProfileErrorType
import com.turnin.domain.profile.model.UserProfile
import com.turnin.domain.profile.model.toUserProfile
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
     * @param forceRefresh 강제 새로고침 (캐시를 무효화하고 데이터를 새롭게 받아온다.)
     */
    operator fun invoke(
        userId: Long,
        forceRefresh: Boolean = false,
    ): Flow<Result<UserProfile, ProfileErrorType>> = flow {
        val userIdVO = UserId(userId)
        emitAll(
            userRepository.getUserProfile(userIdVO, forceRefresh)
                .mapSuccess { coreUserProfile ->
                    coreUserProfile.toUserProfile()
                }
                .mapError { commonError ->
                    ProfileErrorType.CommonError(commonError)
                },
        )
    }
}
