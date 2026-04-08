package com.peekr.domain.keywordEdit.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.common.validation.CommonValidationException
import com.peekr.core.domain.common.validation.toValidationErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.core.domain.util.DomainLogger
import com.peekr.domain.keywordEdit.error.KeywordEditErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** 사용자 키워드 추가 */
class AddUserKeywordUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
    private val logger: DomainLogger,
) {
    private val tag = this::class.java.simpleName

    /**
     * 사용자 키워드를 추가한다.
     *
     * @param keyword 키워드 명
     * @param description 키워드 내용
     *
     * @return [com.peekr.core.domain.userKeyword.model.UserKeyword]
     */
    operator fun invoke(
        keyword: String,
        description: String,
    ): Flow<Result<UserKeyword, KeywordEditErrorType>> = flow {
        emit(Result.Loading)
        val userId = userRepository.getMyUserId()
        if (userId != null) {
            val createUserKeyword = CreateUserKeyword(
                userId = userId,
                keyword = KeywordName(keyword),
                description = KeywordDescription(description),
            )
            emitAll(
                userKeywordRepository
                    .createUserKeyword(createUserKeyword)
                    .mapError { commonError ->
                        KeywordEditErrorType.CommonError(commonError)
                    },
            )
        } else {
            emit(Result.Error(error = KeywordEditErrorType.MyUserIdNotFound))
        }
    }
        .catch { e ->
            when (e) {
                is CommonValidationException ->
                    emit(Result.Error(KeywordEditErrorType.ValidationError(e.toValidationErrorType())))

                else -> {
                    logger.e(tag, e, "Unexpected error occurred.")
                    emit(Result.Error(error = KeywordEditErrorType.Unexpected(e)))
                }
            }
        }
}
