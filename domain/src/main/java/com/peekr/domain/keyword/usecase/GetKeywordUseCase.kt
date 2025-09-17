package com.peekr.domain.keyword.usecase

import com.peekr.domain.common.model.KeywordId
import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.keyword.model.Keyword
import com.peekr.domain.keyword.repository.KeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetKeywordUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository,
) {
    operator fun invoke(keywordId: KeywordId): Flow<Result<Keyword, ErrorType>> =
        keywordRepository.getKeyword(keywordId)
}
