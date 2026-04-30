package com.turnin.domain.keywordEdit.usecase

import javax.inject.Inject

class KeywordEditUseCases @Inject constructor(
    /** @see AddUserKeywordUseCase */
    val add: AddUserKeywordUseCase,
    /** @see UpdateUserKeywordUseCase */
    val update: UpdateUserKeywordUseCase,
    /** @see GetMyKeywordUseCase */
    val getMyKeyword: GetMyKeywordUseCase,
    /** @see ValidateKeywordUseCase */
    val validateKeyword: ValidateKeywordUseCase,
)
