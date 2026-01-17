package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.combineWithResult
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.UserInfo
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 키워드 상세 정보 조회
 *
 * @see invoke
 */
class GetKeywordDetailUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 키워드 상세 정보를 조회한다.
     *
     * @param userId 사용자 ID
     * @param userKeywordId 사용자 키워드 ID
     */
    operator fun invoke(
        userId: Long,
        userKeywordId: Long,
    ): Flow<Result<UserKeywordDetail, KeywordDetailErrorType>> = flow {
        val userIdVO = UserId(userId)
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        emitAll(
            combineWithResult(
                userRepository.getUserProfile(userIdVO),
                userKeywordRepository.getDetail(userIdVO, userKeywordIdVO, false),
            ) { coreUserProfile, userKeywordDetail ->
                val result = userKeywordDetail.data.copy(
                    userInfo = UserInfo(
                        userId = coreUserProfile.data.userId,
                        userName = coreUserProfile.data.name,
                        profileImageUrl = coreUserProfile.data.profileImageUrl,
                    ),
                )
                Result.Success(result)
            }
                .mapError {
                    when (it) {
                        is CommonErrorType -> KeywordDetailErrorType.CommonError(it)
                        else -> KeywordDetailErrorType.Unexpected(null)
                    }
                },
        )
    }
}
