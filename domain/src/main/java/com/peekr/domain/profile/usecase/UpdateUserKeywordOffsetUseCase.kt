package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.util.Result
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 키워드 오프셋 업데이트
 */
class UpdateUserKeywordOffsetUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    /**
     * @param userKeywordId 사용자 키워드 ID
     * @param offsetX 키워드 오프셋 X
     * @param offsetY 키워드 오프셋 Y
     *
     * @see PatchOffset
     */
    operator fun invoke(
        userKeywordId: UserKeywordId,
        offsetX: Float,
        offsetY: Float,
    ): Flow<Result<PatchOffset, ProfileErrorType>> =
        profileRepository.updateOffset(
            userKeywordId = userKeywordId,
            patchOffset = PatchOffset(offsetX.toDouble(), offsetY.toDouble()),
        )
}
