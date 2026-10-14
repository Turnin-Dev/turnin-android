package com.turnin.domain.keywordEdit.usecase

class KeywordEditUseCases(
    /** @see AddUserKeywordUseCase */
    val add: AddUserKeywordUseCase,
    /** @see UpdateUserKeywordUseCase */
    val update: UpdateUserKeywordUseCase,
    /** @see GetMyKeywordUseCase */
    val getMyKeyword: GetMyKeywordUseCase,
    /** @see ValidateKeywordUseCase */
    val validateKeyword: ValidateKeywordUseCase,
)
