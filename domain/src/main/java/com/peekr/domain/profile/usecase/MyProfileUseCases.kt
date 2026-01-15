package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.my.GetMyKeywordsUseCase
import com.peekr.domain.profile.usecase.my.GetMyProfileUseCase
import com.peekr.domain.profile.usecase.my.RefreshMyKeywordsUseCase
import com.peekr.domain.profile.usecase.my.RefreshMyProfileUseCase
import javax.inject.Inject

class MyProfileUseCases @Inject constructor(
    /** @see GetMyProfileUseCase */
    val getMyProfile: GetMyProfileUseCase,
    /** @see RefreshMyProfileUseCase */
    val refreshMyProfile: RefreshMyProfileUseCase,
    /** @see GetMyKeywordsUseCase */
    val getMyKeywords: GetMyKeywordsUseCase,
    /** @see RefreshMyKeywordsUseCase */
    val refreshMyKeywords: RefreshMyKeywordsUseCase,
)
