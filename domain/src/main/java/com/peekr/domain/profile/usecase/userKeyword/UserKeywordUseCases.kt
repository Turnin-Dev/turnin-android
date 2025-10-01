package com.peekr.domain.profile.usecase.userKeyword

import javax.inject.Inject

data class UserKeywordUseCases @Inject constructor(
    val get: GetUserKeywordsUseCase,
    val create: CreateUserKeywordUseCase,
    val patch: PatchUserKeywordUseCase,
    val delete: DeleteUserKeywordUseCase,
)
