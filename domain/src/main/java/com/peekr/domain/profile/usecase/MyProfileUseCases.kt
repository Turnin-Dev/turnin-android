package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.my.GetMyKeywordsUseCase
import com.peekr.domain.profile.usecase.my.GetMyProfileUseCase
import com.peekr.domain.profile.usecase.my.RefreshMyProfileUseCase
import javax.inject.Inject

class MyProfileUseCases @Inject constructor(
    /** @see GetMyProfileUseCase */
    val getMyProfile: GetMyProfileUseCase,
    /** @see GetMyKeywordsUseCase */
    val getMyKeywords: GetMyKeywordsUseCase,
    /** @see RefreshMyProfileUseCase */
    val refreshMyProfile: RefreshMyProfileUseCase,
)
