package com.peekr.core.presentation.feature.keyword.model

/**
 * 키워드 그래프 UI용 사용자 클러스터
 *
 * @property userNode 사용자 노드
 * @property keywordNodes 사용자 키워드 노드 목록
 */
data class UiUserCluster(
    val userNode: UiUserNode,
    val keywordNodes: List<UiKeywordNode>,
)
