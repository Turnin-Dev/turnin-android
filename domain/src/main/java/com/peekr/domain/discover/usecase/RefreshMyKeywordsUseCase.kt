package com.peekr.domain.discover.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.discover.error.DiscoverErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 키워드 새로고침 트리거
 *
 * @see invoke
 */
class RefreshMyKeywordsUseCase @Inject constructor(
    private val repository: UserKeywordRepository,
) {
    /**
     * 나의 키워드 새로고침 트리거를 수행한다.
     */
    operator fun invoke(): Flow<Result<Unit, DiscoverErrorType>> =
        repository.getMyKeywordsRefresh()
            .mapError { commonError ->
                DiscoverErrorType.CommonError(commonError)
            }
}
