package com.peekr.domain.profile.repository

import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.model.Profile
import com.peekr.domain.profile.model.ProfilePatch
import kotlinx.coroutines.flow.Flow

/** 프로필 리포지토리 */
interface ProfileRepository {
    fun getProfile(): Flow<Result<Profile, ErrorType>>

    fun updateProfile(patch: ProfilePatch): Flow<Result<Unit, ErrorType>>
}
