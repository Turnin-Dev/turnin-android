package com.peekr.domain.profile.usecase

import javax.inject.Inject

class ProfileUseCases @Inject constructor(
    val addUserKeyword: AddUserKeywordUseCase,
    val deleteUserKeyword: DeleteUserKeywordUseCase,
    val getProfile: GetProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val updateUserKeywordOffset: UpdateUserKeywordOffsetUseCase,
    val updateUserKeywordDescription: UpdateUserKeywordDescriptionUseCase,
    val validateKeywordDescription: ValidateKeywordDescriptionUseCase,
    val validateKeyword: ValidateKeywordUseCase,
)
