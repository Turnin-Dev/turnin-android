package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** 사용자 키워드 추가 */
class AddUserKeywordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 키워드를 추가한다.
     *
     * @param keyword 키워드 명
     * @param description 키워드 내용
     *
     * @return [UserKeyword]
     */
    operator fun invoke(
        keyword: String,
        description: String,
    ): Flow<Result<UserKeyword, ProfileErrorType>> = flow {
        try {
            emit(Result.Loading)
            val userId = userRepository.getUserId()
            if (userId != null) {
                val createUserKeyword = CreateUserKeyword(
                    userId = userId,
                    keyword = KeywordValue(keyword),
                    description = KeywordDescription(description),
                    offsetX = INITIAL_OFFSET_X,
                    offsetY = INITIAL_OFFSET_Y,
                )
                emitAll(
                    userKeywordRepository
                        .createUserKeyword(createUserKeyword)
                        .mapError { commonError ->
                            ProfileErrorType.CommonError(commonError)
                        },
                )
            } else {
                emit(Result.Error(error = ProfileErrorType.UserNotFound))
            }
        } catch (e: CommonValidationException) {
            val error = ProfileErrorType.ValidationError(e.toValidationErrorType())
            emit(Result.Error(error))
        }
    }
}

private const val INITIAL_OFFSET_X = 0.0
private const val INITIAL_OFFSET_Y = 0.0
