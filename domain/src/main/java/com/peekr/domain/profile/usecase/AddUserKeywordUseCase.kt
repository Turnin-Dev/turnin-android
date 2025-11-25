package com.peekr.domain.profile.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.domain.profile.error.ProfileErrorType
import com.peekr.domain.profile.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** 사용자 키워드 추가 */
class AddUserKeywordUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(
        keyword: String,
        description: String,
    ): Flow<Result<UserKeyword, ProfileErrorType>> = flow {
        try {
            emitAll(
                profileRepository.addKeyword(
                    keyword = KeywordValue(keyword),
                    description = KeywordDescription(description),
                    offsetX = INITIAL_OFFSET_X,
                    offsetY = INITIAL_OFFSET_Y,
                ),
            )
        } catch (e: CommonValidationException) {
            val error = ProfileErrorType.ValidationError(e.toValidationErrorType())
            emit(Result.Error(error))
        }
    }
}

private const val INITIAL_OFFSET_X = 0.0
private const val INITIAL_OFFSET_Y = 0.0
