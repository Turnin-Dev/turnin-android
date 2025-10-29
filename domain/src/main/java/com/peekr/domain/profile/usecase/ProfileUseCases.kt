package com.peekr.domain.profile.usecase

import javax.inject.Inject

class ProfileUseCases @Inject constructor(
    val addUserKeyword: AddUserKeywordUseCase,
    val getProfile: GetProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val updateUserKeywordOffset: UpdateUserKeywordOffsetUseCase,
    val validateKeywordDescription: ValidateKeywordDescriptionUseCase,
    val validateKeyword: ValidateKeywordUseCase,
)
