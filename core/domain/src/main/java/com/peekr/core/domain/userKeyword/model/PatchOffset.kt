package com.peekr.core.domain.userKeyword.model

/**
 * 사용자 키워드 오프셋 수정 모델
 *
 * @property offsetX 오프셋 X
 * @property offsetY 오프셋 Y
 */
data class PatchOffset(
    val offsetX: Double,
    val offsetY: Double,
)
