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
import kotlinx.coroutines.flow.last

/**
 * 나의 키워드 상세 정보 조회
 *
 * @see invoke
 */
class GetMyKeywordDetailUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 키워드 상세 정보를 조회한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(
        userId: Long,
        userKeywordId: Long,
    ): Flow<Result<KeywordDetail, KeywordDetailErrorType>> = flow {
        val userIdVO = UserId(userId)
        val userKeywordIdVO = UserKeywordId(userKeywordId)

        // 1. 로컬에서 나의 키워드 상세 정보 조회
        val myUserKeywordDetail =
            userKeywordRepository.getMyDetailFromLocal(userKeywordIdVO).last()

        // 2. 데이터가 존재하면 그대로 방출
        if (myUserKeywordDetail != null) {
            emit(Result.Success(myUserKeywordDetail.toKeywordDetail()))
            return@flow
        }

        // 3. 존재하지 않다면 데이터를 네트워크에서 조회
        emitAll(
            userKeywordRepository.getDetailRefresh(userIdVO, userKeywordIdVO)
                .mapSuccess { it.toKeywordDetail() }
                .mapError { commonError ->
                    KeywordDetailErrorType.CommonError(commonError)
                },
        )
    }
}
