package com.peekr.domain.profile.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject

/**
 * 사용자 키워드 설명 업데이트
 */
class UpdateUserKeywordDescriptionUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    /**
     * @param userKeywordId 사용자 키워드 ID
     * @param description 키워드 설명
     *
     * @see PatchOffset
     */
    operator fun invoke(
        userKeywordId: UserKeywordId,
        description: String,
    ) = profileRepository.updateDescription(
        userKeywordId = userKeywordId,
        patchDescription = PatchDescription(KeywordDescription(description)),
    )
    // 만약, KeywordDescription에서 유효성 검사가 추가 된다면 예외 처리를 추가 해야한다.
}
