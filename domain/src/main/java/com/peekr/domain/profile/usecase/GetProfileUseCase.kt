package com.peekr.domain.profile.usecase

import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 프로필 조회 */
class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<Result<Profile, ProfileErrorType>> =
        profileRepository.getProfile()
}
