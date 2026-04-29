package com.turnin.presentation.discover.model

import com.turnin.core.domain.discover.model.DiscoverKeyword

/**
 * UI용 탐색 키워드 모델
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 */
data class UiDiscoverKeyword(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
)

fun DiscoverKeyword.toUiModel(): UiDiscoverKeyword =
    UiDiscoverKeyword(
        userKeywordId = userKeywordId.value,
        keywordId = keywordId.value,
        keywordName = keywordName.value,
    )
