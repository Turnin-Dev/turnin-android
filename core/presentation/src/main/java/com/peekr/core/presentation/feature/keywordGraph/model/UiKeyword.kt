package com.peekr.core.presentation.feature.keywordGraph.model

import com.peekr.core.presentation.ui.model.UiDisplayId

/**
 * UI용 키워드
 *
 * @property id 키워드 ID
 * @property name 키워드 명
 * @property offsetX 키워드가 UI 상에 위치할 오프셋 X
 * @property offsetY 키워드가 UI 상에 위치할 오프셋 Y
 * @property createdBy 키워드 최초 생성자
 * @property createdAt 키워드 생성일
 * @property updatedAt 키워드 수정일
 */
data class UiKeyword(
    val id: Long,
    val name: String,
    val offsetX: Float,
    val offsetY: Float,
    val createdBy: UiDisplayId,
    val createdAt: Long,
    val updatedAt: Long,
) {
    companion object {
        val samples = List(5) {
            UiKeyword(it.toLong(), "Label $it", 0f, 0f, UiDisplayId("displayId$it"), 0L, 0L)
        }
    }
}
