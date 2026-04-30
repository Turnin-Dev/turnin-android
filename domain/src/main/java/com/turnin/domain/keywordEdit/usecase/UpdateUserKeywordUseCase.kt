package com.turnin.domain.keywordEdit.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.PatchUserKeyword
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.keywordEdit.error.KeywordEditErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 사용자 키워드 수정
 *
 * @see invoke
 */
class UpdateUserKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 사용자 키워드를 수정한다.
     *
     * @param userKeywordId 사용자 키워드 ID
     * @param keywordName 키워드 명
     * @param description 키워드 내용
     */
    operator fun invoke(
        userKeywordId: Long,
        keywordName: String,
        description: String,
    ): Flow<Result<Unit, KeywordEditErrorType>> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        val keywordNameVO = KeywordName(keywordName)
        val descriptionVO = KeywordDescription(description)
        val patchUserKeyword = PatchUserKeyword(
            userKeywordId = userKeywordIdVO,
            keywordName = keywordNameVO,
            description = descriptionVO,
        )

        emitAll(
            userKeywordRepository.update(patchUserKeyword)
                .mapError { commonError ->
                    when (commonError) {
                        is CommonErrorType.Network.NotFound -> KeywordEditErrorType.UpdateFailed
                        else -> KeywordEditErrorType.CommonError(commonError)
                    }
                },
        )
    }
}
