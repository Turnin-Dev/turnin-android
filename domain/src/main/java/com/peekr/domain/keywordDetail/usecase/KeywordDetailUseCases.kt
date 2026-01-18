package com.peekr.domain.keywordDetail.usecase

import javax.inject.Inject

class KeywordDetailUseCases @Inject constructor(
    /** @see GetKeywordDetailUseCase */
    val getKeywordDetail: GetKeywordDetailUseCase,
)
