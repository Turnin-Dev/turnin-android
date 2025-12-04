package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 키워드 삭제
 */
class DeleteUserKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 키워드를 삭제한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(userKeywordId: UserKeywordId): Flow<Result<Unit, ProfileErrorType>> =
        userKeywordRepository
            .deleteUserKeyword(userKeywordId)
            .mapError { commonError ->
                ProfileErrorType.CommonError(commonError)
            }
}
