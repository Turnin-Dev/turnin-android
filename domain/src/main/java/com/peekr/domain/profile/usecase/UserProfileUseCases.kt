package com.peekr.domain.profile.usecase

import com.peekr.domain.profile.usecase.user.DeleteBlockUseCase
import com.peekr.domain.profile.usecase.user.GetCachedUserProfileUseCase
import com.peekr.domain.profile.usecase.user.GetUserKeywordsUseCase
import com.peekr.domain.profile.usecase.user.GetUserProfileUseCase
import com.peekr.domain.profile.usecase.user.UpdateFriendStateUseCase
import javax.inject.Inject

class UserProfileUseCases @Inject constructor(
    /**
     * 사용자 프로필 조회
     * @see GetUserProfileUseCase
     */
    val getUserProfile: GetUserProfileUseCase,
    /**
     * 사용자 프로필 캐시 조회
     * @see GetCachedUserProfileUseCase
     */
    val getCachedUserProfile: GetCachedUserProfileUseCase,
    /**
     * 사용자 키워드 조회
     * @see GetUserKeywordsUseCase
     */
    val getUserKeywords: GetUserKeywordsUseCase,
    /**
     * 사용자 친구 상태 업데이트
     * @see UpdateFriendStateUseCase
     */
    val updateFriendStatus: UpdateFriendStateUseCase,
    /**
     * 사용자 차단 해제
     * @see DeleteBlockUseCase
     */
    val deleteBlock: DeleteBlockUseCase,
)
