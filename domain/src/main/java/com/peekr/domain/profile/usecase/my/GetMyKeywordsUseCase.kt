package com.peekr.domain.profile.usecase.my

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import com.peekr.domain.profile.error.ProfileErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 나의 사용자 키워드 조회
 *
 * @see invoke
 */
class GetMyKeywordsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 사용자 키워드를 조회한다.
     */
    operator fun invoke(): Flow<Result<UserKeywords, ProfileErrorType>> = flow {
        userRepository.getUserId()?.let { userId ->
            emitAll(
                userKeywordRepository.getUserKeywords(userId)
                    .mapError { commonError ->
                        ProfileErrorType.CommonError(commonError)
                    },
            )
        } ?: emit(Result.Error(ProfileErrorType.MyUserIdNotFound))
    }
}
