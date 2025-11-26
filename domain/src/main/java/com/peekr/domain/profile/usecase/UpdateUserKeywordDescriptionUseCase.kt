package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 키워드 설명 업데이트
 */
class UpdateUserKeywordDescriptionUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 키워드 설명 수정
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param description 키워드 설명
     *
     * @see PatchDescription
     */
    operator fun invoke(
        userKeywordId: UserKeywordId,
        description: String,
    ): Flow<Result<PatchDescription, ProfileErrorType>> =
        userKeywordRepository
            .patchDescription(userKeywordId, PatchDescription(KeywordDescription(description)))
            .mapError { userKeywordError ->
                ProfileErrorType.UserKeywordError(userKeywordError)
            }
    // 만약, KeywordDescription에서 유효성 검사가 추가 된다면 예외 처리를 추가 해야한다.
}
