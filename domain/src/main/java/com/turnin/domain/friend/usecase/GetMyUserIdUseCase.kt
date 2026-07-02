package com.turnin.domain.friend.usecase

import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import javax.inject.Inject

/**
 * 나의 사용자 ID 캐시 조회
 *
 * @see invoke
 */
class GetMyUserIdUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 캐시에서 나의 사용자 ID를 조회한다.
     */
    operator fun invoke(): UserId? =
        userRepository.myProfile.value?.userId
}
