package com.turnin.domain.keywordDetail.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.mapSuccess
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.user.usecase.GetMyUserIdUseCase
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.core.domain.util.catchAndLog
import com.turnin.domain.keywordDetail.error.KeywordDetailErrorType
import com.turnin.domain.keywordDetail.model.KeywordDetail
import com.turnin.domain.keywordDetail.model.toKeywordDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

/**
 * 키워드 상세 정보 조회
 *
 * @see invoke
 */
class GetKeywordDetailUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
    private val getMyUserIdUseCase: GetMyUserIdUseCase,
    private val logger: DomainLogger,
) {
    private val tag = this::class.java.simpleName

    /**
     * 키워드 상세 정보를 조회한다.
     *
     * @param userId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(
        userId: Long,
        userKeywordId: Long,
    ): Flow<Result<KeywordDetail, KeywordDetailErrorType>> = flow {
        // 1. 데이터 준비
        val userIdVO = UserId(userId)
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        val myUserIdVO = getMyUserIdUseCase()
        if (myUserIdVO == null) {
            emit(Result.Error(KeywordDetailErrorType.UserIdNotFound))
            return@flow
        }

        // 2. 나의 키워드인지 사용자 키워드인지 판별하여 조회
        if (myUserIdVO == userIdVO) {
            emitAll(getMyKeywordDetail(userIdVO, userKeywordIdVO))
        } else {
            emitAll(getUserKeywordDetail(userIdVO, userKeywordIdVO))
        }
    }
        .catchAndLog(logger, tag) { e ->
            emit(Result.Error(KeywordDetailErrorType.Unexpected(e)))
        }

    private fun getMyKeywordDetail(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDetail, KeywordDetailErrorType>> = flow {
        // 1. 로컬에서 나의 키워드 상세 정보 조회
        val myUserKeywordDetail =
            userKeywordRepository.getMyDetailFromLocal(userKeywordId).last()

        // 2. 데이터가 존재하면 그대로 방출
        if (myUserKeywordDetail != null) {
            emit(Result.Success(myUserKeywordDetail.toKeywordDetail()))
            return@flow
        }

        // 3. 존재하지 않다면 데이터를 네트워크에서 조회
        emitAll(
            userKeywordRepository.getDetailRefresh(userId, userKeywordId)
                .mapSuccess { it.toKeywordDetail() }
                .mapError { commonError ->
                    KeywordDetailErrorType.CommonError(commonError) as KeywordDetailErrorType
                },
        )
    }

    private fun getUserKeywordDetail(
        userId: UserId,
        userKeywordId: UserKeywordId,
    ): Flow<Result<KeywordDetail, KeywordDetailErrorType>> =
        userKeywordRepository.getDetail(userId, userKeywordId)
            .mapSuccess { it.toKeywordDetail() }
            .mapError { commonError ->
                KeywordDetailErrorType.CommonError(commonError)
            }
}
