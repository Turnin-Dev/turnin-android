package com.peekr.core.presentation.feature.keyword.model

/**
 * 키워드 그래프 UI용 키워드 노드
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keyword 키워드
 */
data class UiKeywordNode(
    val userKeywordId: Long,
    val keywordId: Long,
    val keyword: String,
)
