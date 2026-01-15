package com.peekr.domain.keywordDetail.usecase

import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject

/**
 * 키워드 상세 정보 조회
 *
 * @see invoke
 */
class GetKeywordDetailUseCase @Inject constructor(
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 키워드 상세 정보를 조회한다.
     */
    operator fun invoke() {
    }
}
