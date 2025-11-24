package com.peekr.data.keywordDetail.repository

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.mapError
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class KeywordDetailRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) : KeywordDetailRepository {
    override fun getUserId(): Flow<Result<UserId, KeywordDetailErrorType>> =
        userRepository
            .getUserId()
            .mapError { userErrorType ->
                KeywordDetailErrorType.UserError(userErrorType)
            }

    override fun getDescription(
        userKeywordId: Long,
    ): Flow<Result<KeywordDescription, KeywordDetailErrorType>> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        emitAll(
            userKeywordRepository
                .getDescription(userKeywordIdVO)
                .mapError { userKeywordErrorType ->
                    KeywordDetailErrorType.UserKeywordError(userKeywordErrorType)
                },
        )
    }

    override fun updateDescription(
        userKeywordId: Long,
        description: String,
    ): Flow<Result<PatchDescription, KeywordDetailErrorType>> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        val descriptionVO = KeywordDescription(description)
        val patchDescription = PatchDescription(descriptionVO)
        emitAll(
            userKeywordRepository.patchDescription(userKeywordIdVO, patchDescription)
                .mapError { userKeywordErrorType ->
                    KeywordDetailErrorType.UserKeywordError(userKeywordErrorType)
                },
        )
    }
}
