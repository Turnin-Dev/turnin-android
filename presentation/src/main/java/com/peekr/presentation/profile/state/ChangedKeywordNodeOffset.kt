package com.peekr.presentation.profile.state

/**
 * 변경된 사용자 키워드 노드 오프셋
 *
 * @property offsetX 업데이트된 사용자 키워드 오프셋 X
 * @property offsetY 업데이트된 사용자 키워드 오프셋 Y
 */
data class ChangedKeywordNodeOffset(
    val offsetX: Float,
    val offsetY: Float,
)
