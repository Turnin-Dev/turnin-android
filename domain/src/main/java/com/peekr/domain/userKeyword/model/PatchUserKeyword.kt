package com.peekr.domain.userKeyword.model

/**
 * 사용자 키워드 수정 요청
 *
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 */
data class PatchUserKeyword(
    val offsetX: Double,
    val offsetY: Double,
    val description: String,
)
