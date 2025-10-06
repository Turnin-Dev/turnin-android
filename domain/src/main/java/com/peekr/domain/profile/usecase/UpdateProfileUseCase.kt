package com.peekr.domain.profile.usecase

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.ProfilePatch
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UpdateProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(patch: ProfilePatch): Flow<Result<Unit, ErrorType>> =
        profileRepository.updateProfile(patch)
}
