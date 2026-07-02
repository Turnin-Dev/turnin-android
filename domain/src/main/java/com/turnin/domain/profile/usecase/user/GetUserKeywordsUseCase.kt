package com.turnin.domain.profile.usecase.user

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.mapSuccess
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.userKeyword.model.UserKeyword
import com.turnin.core.domain.userKeyword.model.toNonDetail
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.profile.error.ProfileErrorType
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
     * @param forceRefresh 강제 새로고침 (캐시를 무효화하고 데이터를 새롭게 받아온다.)
     */
    operator fun invoke(
        userId: Long,
        forceRefresh: Boolean = false,
    ): Flow<Result<List<UserKeyword>, ProfileErrorType>> =
        flow {
            val userIdVO = UserId(userId)
            emitAll(
                userKeywordRepository.getUserKeywords(userIdVO, forceRefresh)
                    .mapSuccess {
                        it.map { it.toNonDetail() }
                    }
                    .mapError { commonError ->
                        ProfileErrorType.CommonError(commonError)
                    },
            )
        }
}
