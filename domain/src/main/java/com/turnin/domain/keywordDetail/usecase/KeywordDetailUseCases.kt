package com.turnin.domain.keywordDetail.usecase

class KeywordDetailUseCases(
    /** @see GetKeywordDetailUseCase */
    val getKeywordDetail: GetKeywordDetailUseCase,
    /** @see RefreshKeywordDetailUseCase */
    val refreshKeywordDetail: RefreshKeywordDetailUseCase,
    /** @see DeleteKeywordUseCase */
    val deleteKeyword: DeleteKeywordUseCase,
)
