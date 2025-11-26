package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.usecase.GetUserIdUseCase
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 내 키워드 확인
 */
class CheckMyKeywordUseCase @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
) {
    operator fun invoke(): Flow<Result<UserId, KeywordDetailErrorType>> =
        getUserIdUseCase()
            .map { idResult ->
                when (idResult) {
                    Result.Loading -> Result.Loading
                    is Result.Error -> {
                        val error = KeywordDetailErrorType.CommonError(idResult.error)
                        Result.Error(error)
                    }

                    is Result.Success -> {
                        val userId = idResult.data
                        if (userId != null) {
                            Result.Success(userId)
                        } else {
                            Result.Error(KeywordDetailErrorType.UserIdNotFound)
                        }
                    }
                }
            }
}
