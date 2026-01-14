package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.model.MyProfile
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 나의 프로필 조회
 *
 * @see invoke
 */
class GetMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 나의 프로필을 로컬 데이터에서 조회한다.
     */
    operator fun invoke(): Flow<MyProfile?> =
        userRepository.getMyProfile().map {
            it?.let {
                MyProfile(
                    userId = it.userId,
                    displayId = it.displayId,
                    name = it.name,
                    profileImageUrl = it.profileImageUrl,
                    introduce = it.introduce,
                    friendsCount = it.friendsCount,
                    lastLoginAt = it.lastLoginAt,
                    active = it.active,
                )
            }
        }
}
