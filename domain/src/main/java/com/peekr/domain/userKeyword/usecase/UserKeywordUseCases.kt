package com.peekr.domain.userKeyword.usecase

import javax.inject.Inject

data class UserKeywordUseCases @Inject constructor(
    val get: GetUserKeywordsUseCase,
    val create: CreateUserKeywordUseCase,
    val patch: PatchUserKeywordUseCase,
    val delete: DeleteUserKeywordUseCase,
)
