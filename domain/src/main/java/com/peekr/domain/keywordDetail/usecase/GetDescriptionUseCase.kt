package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.util.Result
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 키워드 설명 조회
 */
class GetDescriptionUseCase @Inject constructor(
    private val keywordDetailRepository: KeywordDetailRepository,
) {
    operator fun invoke(
        userKeywordId: Long,
    ): Flow<Result<KeywordDescription, KeywordDetailErrorType>> =
        keywordDetailRepository.getDescription(userKeywordId)
}
