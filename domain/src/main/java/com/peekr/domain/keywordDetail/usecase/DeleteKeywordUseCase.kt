package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 키워드 삭제
 *
 * @see invoke
 */
class DeleteKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 키워드를 삭제한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(userKeywordId: Long): Flow<Result<Unit, KeywordDetailErrorType>> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        emitAll(
            userKeywordRepository.deleteUserKeyword(userKeywordIdVO)
                .mapError { commonError ->
                    KeywordDetailErrorType.CommonError(commonError)
                },
        )
    }
}
