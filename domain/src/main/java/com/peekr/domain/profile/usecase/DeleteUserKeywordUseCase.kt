package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DeleteUserKeywordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(userKeywordId: UserKeywordId): Flow<Result<Unit, ProfileErrorType>> =
        profileRepository.deleteKeyword(userKeywordId)
}
