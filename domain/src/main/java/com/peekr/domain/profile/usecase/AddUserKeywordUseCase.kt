package com.peekr.domain.profile.usecase

import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** 사용자 키워드 추가 */
class AddUserKeywordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(
        keywordName: String,
        keywordDesc: String?,
    ): Flow<Result<UserKeyword, ErrorType>> =
        profileRepository.addKeyword(
            keywordName = keywordName,
            keywordDesc = keywordDesc,
            offsetX = INITIAL_OFFSET_X,
            offsetY = INITIAL_OFFSET_Y,
        )
}

private const val INITIAL_OFFSET_X = 0.0
private const val INITIAL_OFFSET_Y = 0.0
