package com.turnin.domain.profile.usecase.my

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.profile.error.ProfileErrorType
import kotlinx.coroutines.flow.Flow

/**
 * 나의 키워드 리스트 새로고침
 *
 * @see invoke
 */
class RefreshMyKeywordsUseCase(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(): Flow<Result<Unit, ProfileErrorType>> =
        userKeywordRepository.getMyKeywordsRefresh()
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
