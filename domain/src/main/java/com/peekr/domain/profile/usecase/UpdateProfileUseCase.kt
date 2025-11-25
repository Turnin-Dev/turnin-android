package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 프로필 수정 */
class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(patch: ProfilePatch): Flow<Result<Unit, ProfileErrorType>> =
        profileRepository.updateProfile(patch)
}
