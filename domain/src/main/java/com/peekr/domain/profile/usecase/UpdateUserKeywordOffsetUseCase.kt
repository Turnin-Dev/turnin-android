package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 키워드 오프셋 업데이트
 */
class UpdateUserKeywordOffsetUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 키워드 오프셋을 업데이트한다.
     *
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
        userKeywordRepository
            .patchOffset(userKeywordId, PatchOffset(offsetX.toDouble(), offsetY.toDouble()))
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
