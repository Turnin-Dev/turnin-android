package com.peekr.presentation.discover.model

import androidx.compose.runtime.Immutable
import com.peekr.core.domain.discover.model.DiscoverContext

/**
 * UI용 탐색 컨텍스트 모델
 *
 * 탐색에 필요한 정보를 담고 있다.
 *
 * - 담고 있는 정보: 사용자 정보 일부 + 키워드 정보 일부
 *
 * @property user 탐색용 사용자
 * @property keywords 탐색용 키워드 리스트
 */
@Immutable
data class UiDiscoverContext(
    val user: UiDiscoverUser,
    val keywords: List<UiDiscoverKeyword>,
) {
    companion object {
        val sample = UiDiscoverContext(
            user = UiDiscoverUser(
                userId = 1L,
                userName = "홍길동",
                displayId = "Hong123",
                profileImageUrl = null,
            ),
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
        user = user.toUiModel(),
        keywords = keywords.map { it.toUiModel() },
    )
