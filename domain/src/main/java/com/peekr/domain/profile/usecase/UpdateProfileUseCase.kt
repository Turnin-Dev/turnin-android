package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.model.toUserPatch
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 프로필 수정 */
class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 프로필을 수정한다.
     *
     * @param patch [ProfilePatch] 프로필 수정 패치
     */
    operator fun invoke(patch: ProfilePatch): Flow<Result<Unit, ProfileErrorType>> =
        userRepository
            .updateUser(patch.toUserPatch())
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
