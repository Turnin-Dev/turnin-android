package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 키워드 설명 업데이트
 */
class UpdateDescriptionUseCase @Inject constructor(
    private val keywordDetailRepository: KeywordDetailRepository,
) {
    operator fun invoke(
        userKeywordId: Long,
        description: String,
    ): Flow<Result<PatchDescription, KeywordDetailErrorType>> =
        keywordDetailRepository.updateDescription(userKeywordId, description)
}
