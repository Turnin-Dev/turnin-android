package com.peekr.domain.discover.usecase

import com.peekr.core.domain.discover.model.DiscoverContext
import com.peekr.core.domain.discover.model.DiscoverKeyword
import com.peekr.core.domain.discover.model.DiscoverUser
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

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
     */
    operator fun invoke(): Flow<DiscoverContext> =
        combine(
            userRepository.myProfile,
            userKeywordRepository.getMyKeywords(),
        ) { coreMyProfile, myKeywords ->
            coreMyProfile?.let {
                DiscoverContext(
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
            .filterNotNull()
}
