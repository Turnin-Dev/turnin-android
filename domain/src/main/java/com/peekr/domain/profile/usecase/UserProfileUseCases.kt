package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.user.DeleteBlockUseCase
import com.peekr.domain.profile.usecase.user.GetUserKeywordsUseCase
import com.peekr.domain.profile.usecase.user.GetUserProfileUseCase
import com.peekr.domain.profile.usecase.user.UpdateFriendStateUseCase
import javax.inject.Inject

class UserProfileUseCases @Inject constructor(
    /** @see GetUserProfileUseCase */
    val getUserProfile: GetUserProfileUseCase,
    /** @see GetUserKeywordsUseCase */
    val getUserKeywords: GetUserKeywordsUseCase,
    /** @see UpdateFriendStateUseCase */
    val updateFriendStatus: UpdateFriendStateUseCase,
    /** @see DeleteBlockUseCase */
    val deleteBlock: DeleteBlockUseCase,
)
