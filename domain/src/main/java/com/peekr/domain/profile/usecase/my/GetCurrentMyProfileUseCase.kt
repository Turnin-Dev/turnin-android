package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.model.MyProfile
import com.peekr.domain.profile.model.toMyProfile
import javax.inject.Inject

/**
 * 나의 프로필을 즉시 조회
 *
 * @see invoke
 */
class GetCurrentMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 나의 프로필을 로컬 데이터에서 조회한다.
     */
    operator fun invoke(): MyProfile? =
        userRepository.myProfile.value?.toMyProfile()
}
