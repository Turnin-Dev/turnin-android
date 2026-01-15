package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 키워드 리스트 새로고침
 *
 * @see invoke
 */
class RefreshMyKeywordsUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Result<Unit, ProfileErrorType>> =
        userRepository.getMyKeywordsRefresh()
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
