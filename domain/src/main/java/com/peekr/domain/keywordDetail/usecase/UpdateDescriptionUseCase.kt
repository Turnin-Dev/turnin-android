package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 키워드 설명 업데이트
 */
class UpdateDescriptionUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 키워드 설명을 업데이트한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param description 키워드 설명
     *
     * @return [PatchDescription] 키워드 설명 수정 모델
     */
    operator fun invoke(
        userKeywordId: Long,
        description: String,
    ): Flow<Result<PatchDescription, KeywordDetailErrorType>> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        val descriptionVO = KeywordDescription(description)
        val patchDescription = PatchDescription(descriptionVO)
        emitAll(
            userKeywordRepository.patchDescription(userKeywordIdVO, patchDescription)
                .mapError { commonError ->
                    KeywordDetailErrorType.CommonError(commonError)
                },
        )
    }
}
