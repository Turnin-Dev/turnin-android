package com.peekr.domain.profile.usecase.keyword

import com.peekr.core.domain.keyword.model.Keyword
import com.peekr.core.domain.keyword.repository.KeywordRepository
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CreateKeywordUseCase @Inject constructor(
    private val keywordRepository: KeywordRepository,
) {
    operator fun invoke(keyword: String): Flow<Result<Keyword, ErrorType>> =
        keywordRepository.createKeyword(keyword)
}
