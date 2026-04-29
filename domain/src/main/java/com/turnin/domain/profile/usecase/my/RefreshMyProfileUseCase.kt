package com.turnin.domain.profile.usecase.my

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 프로필 새로고침
 *
 * @see invoke
 */
class RefreshMyProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * 나의 프로필을 새로고침한다.
     *
     * 새로고침 시 서버에서 데이터를 가져와 로컬에 저장한다.
     */
    operator fun invoke(): Flow<Result<Unit, ProfileErrorType>> =
        userRepository.getMyProfileRefresh()
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
