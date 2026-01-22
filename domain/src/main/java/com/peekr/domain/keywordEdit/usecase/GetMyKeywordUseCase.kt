package com.peekr.domain.keywordEdit.usecase

import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 나의 키워드 조회
 *
 * @param invoke
 */
class GetMyKeywordUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 키워드를 로컬에서 단발성으로 조회한다.
     */
    operator fun invoke(userKeywordId: Long): Flow<UserKeywordDetail?> = flow {
        val userKeywordIdVO = UserKeywordId(userKeywordId)
        emitAll(userKeywordRepository.getMyDetailFromLocal(userKeywordIdVO))
    }
}
