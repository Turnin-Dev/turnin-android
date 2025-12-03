package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.user.GetUserProfileUseCase
import javax.inject.Inject

class UserProfileUseCases @Inject constructor(
    val getUserProfile: GetUserProfileUseCase,
)
