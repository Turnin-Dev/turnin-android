package com.peekr.domain.discover.usecase

import com.peekr.core.domain.discover.model.DiscoverContext
import com.peekr.core.domain.discover.model.DiscoverKeyword
import com.peekr.core.domain.discover.model.DiscoverUser
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull

/**
 * 나의 탐색 컨텍스트 조회
 *
 * @see invoke
 */
class GetMyDiscoverContextUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userKeywordRepository: UserKeywordRepository,
) {
    /**
     * 나의 탐색 컨텍스트를 조회한다.
     *
     * 나의 프로필 조회에 실패하거나 존재하지 않으면 `null`을 반환한다.
     */
    suspend operator fun invoke(): DiscoverContext? {
        val coreMyProfile = userRepository.getMyProfile().firstOrNull()
        if (coreMyProfile == null) return null
        val myKeywords = userKeywordRepository.getMyKeywords().firstOrNull()
        if (myKeywords == null) return null
        return DiscoverContext(
            user = DiscoverUser(
                userId = coreMyProfile.userId,
                userName = coreMyProfile.name,
                displayId = coreMyProfile.displayId,
                profileImageUrl = coreMyProfile.profileImageUrl,
            ),
            keywords = myKeywords.map { userKeyword ->
                DiscoverKeyword(
                    userKeywordId = userKeyword.id,
                    keywordId = userKeyword.keywordId,
                    keywordName = userKeyword.keyword,
                )
            },
        )
    }
}
