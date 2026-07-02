package com.turnin.domain.profile.usecase.my

import com.turnin.core.domain.userKeyword.model.UserKeyword
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 나의 사용자 키워드 조회
 *
 * @see invoke
 */
class GetMyKeywordsUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 사용자 키워드를 조회한다.
     */
    operator fun invoke(): Flow<List<UserKeyword>> =
        userKeywordRepository.getMyKeywords()
}
