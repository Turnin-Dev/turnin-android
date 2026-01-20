package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.mapSuccess
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.model.KeywordDetail
import com.peekr.domain.keywordDetail.model.toKeywordDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 키워드 상세 정보 새로고침
 *
 * @see invoke
 */
class RefreshKeywordDetailUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 키워드 상세 정보를 새로고침한다.
     *
     * @param userId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(
        userId: Long,
        userKeywordId: Long,
    ): Flow<Result<KeywordDetail, KeywordDetailErrorType>> = flow {
        val userIdVO = UserId(userId)
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        emitAll(
            userKeywordRepository.getDetailRefresh(userIdVO, userKeywordIdVO)
                .mapSuccess { it.toKeywordDetail() }
                .mapError { commonError ->
                    KeywordDetailErrorType.CommonError(commonError)
                },
        )
    }
}
