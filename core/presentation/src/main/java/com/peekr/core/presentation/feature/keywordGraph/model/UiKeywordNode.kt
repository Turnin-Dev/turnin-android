package com.peekr.core.presentation.feature.keywordGraph.model

/**
 * 키워드 그래프 UI용 키워드 노드
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드
 */
data class UiKeywordNode(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
)
