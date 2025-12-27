package com.peekr.core.presentation.feature.keywordGraph.model

/**
 * 키워드 그래프 UI용 사용자 노드
 *
 * @property userId 사용자 ID
 * @property profileImageUrl 프로필 사진 url
 */
data class UiUserNode(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
)
