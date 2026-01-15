package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 사용자 키워드 리스트 조회
 *
 * @see invoke
 */
class GetUserKeywordsUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 키워드 리스트를 조회한다.
     *
     * @param userId 조회할 사용자 ID
     */
    operator fun invoke(userId: Long): Flow<Result<List<UserKeywordDetail>, ProfileErrorType>> =
        flow {
            val userIdVO = UserId(userId)
            emitAll(
                userKeywordRepository.getUserKeywords(userIdVO)
                    .mapError { commonError ->
                        ProfileErrorType.CommonError(commonError)
                    },
            )
        }
}
