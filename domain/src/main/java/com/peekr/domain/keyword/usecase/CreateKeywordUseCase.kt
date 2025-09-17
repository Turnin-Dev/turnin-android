package com.peekr.domain.keyword.usecase

import com.peekr.domain.common.util.ErrorType
import com.peekr.domain.common.util.Result
import com.peekr.domain.keyword.model.Keyword
import com.peekr.domain.keyword.repository.KeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateKeywordUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository,
) {
    operator fun invoke(keyword: String): Flow<Result<Keyword, ErrorType>> =
        keywordRepository.createKeyword(keyword)
}
