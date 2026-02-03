package com.peekr.presentation.discover.model

import com.peekr.core.domain.discover.model.DiscoverContext

/**
 * UI용 탐색 컨텍스트 모델
 *
 * 탐색에 필요한 정보를 담고 있다.
 *
 * - 담고 있는 정보: 사용자 정보 일부 + 키워드 정보 일부
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property displayId 사용자 표시 ID
 * @property profileImageUrl 사용자 프로필 url
 * @property keywords 탐색용 키워드 리스트
 */
data class UiDiscoverContext(
    val userId: Long,
    val userName: String,
    val displayId: String,
    val profileImageUrl: String?,
    val keywords: List<UiDiscoverKeyword>,
) {
    companion object {
        val sample = UiDiscoverContext(
            userId = 1L,
            userName = "홍길동",
            displayId = "Honggd123",
            profileImageUrl = null,
            keywords = List(5) {
                UiDiscoverKeyword(
                    userKeywordId = it + 1L,
                    keywordId = it + 1L,
                    keywordName = "키워드키워드키워드$it",
                )
            },
        )
    }
}

fun DiscoverContext.toUiModel(): UiDiscoverContext =
    UiDiscoverContext(
        userId = user.userId.value,
        userName = user.userName.value,
        displayId = user.displayId.value,
        profileImageUrl = user.profileImageUrl,
        keywords = keywords.map { it.toUiModel() },
    )
