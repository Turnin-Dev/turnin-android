package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.user.GetUserProfileUseCase
import com.peekr.domain.profile.usecase.user.UpdateFriendStateUseCase
import javax.inject.Inject

class UserProfileUseCases @Inject constructor(
    val getUserProfile: GetUserProfileUseCase,
    val updateFriendStatus: UpdateFriendStateUseCase,
)
