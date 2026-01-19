package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 키워드 리스트 새로고침
 *
 * @see invoke
 */
class RefreshMyKeywordsUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    operator fun invoke(): Flow<Result<Unit, ProfileErrorType>> =
        userKeywordRepository.getMyKeywordsRefresh()
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
