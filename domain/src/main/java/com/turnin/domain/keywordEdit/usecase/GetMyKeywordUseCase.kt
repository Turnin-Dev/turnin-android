package com.turnin.domain.keywordEdit.usecase

import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.core.domain.util.DomainLogger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
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
    private val logger: DomainLogger,
) {
    private val tag = this::class.java.simpleName

    /**
     * 나의 키워드를 로컬에서 단발성으로 조회한다.
     */
    operator fun invoke(userKeywordId: Long): Flow<UserKeywordDetail?> = flow {
        try {
            val userKeywordIdVO = UserKeywordId(userKeywordId)
            emitAll(userKeywordRepository.getMyDetailFromLocal(userKeywordIdVO))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            logger.e(tag, e, "Unexpected error occurred.")
            emit(null)
        }
    }
}
