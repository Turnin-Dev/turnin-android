package com.turnin.domain.profile.usecase.my

import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.profile.model.MyProfile
import com.turnin.domain.profile.model.toMyProfile
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
        userRepository.myProfile.map { it?.toMyProfile() }
}
