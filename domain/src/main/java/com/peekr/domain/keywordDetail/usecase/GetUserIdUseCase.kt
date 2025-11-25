package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserId
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 ID 조회
 */
class GetUserIdUseCase @Inject constructor(
    private val keywordDetailRepository: KeywordDetailRepository,
) {
    operator fun invoke(): Flow<Result<UserId, KeywordDetailErrorType>> =
        keywordDetailRepository.getUserId()
}
