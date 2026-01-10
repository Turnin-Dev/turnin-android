package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.my.DeleteUserKeywordUseCase
import com.peekr.domain.profile.usecase.my.GetMyProfileUseCase
import com.peekr.domain.profile.usecase.my.UpdateProfileUseCase
import com.peekr.domain.profile.usecase.my.UpdateUserKeywordDescriptionUseCase
import com.peekr.domain.profile.usecase.my.ValidateKeywordDescriptionUseCase
import com.peekr.domain.profile.usecase.my.ValidateKeywordUseCase
import javax.inject.Inject

class MyProfileUseCases @Inject constructor(
    val deleteUserKeyword: DeleteUserKeywordUseCase,
    val getMyProfile: GetMyProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val updateUserKeywordDescription: UpdateUserKeywordDescriptionUseCase,
    val validateKeywordDescription: ValidateKeywordDescriptionUseCase,
    val validateKeyword: ValidateKeywordUseCase,
)
