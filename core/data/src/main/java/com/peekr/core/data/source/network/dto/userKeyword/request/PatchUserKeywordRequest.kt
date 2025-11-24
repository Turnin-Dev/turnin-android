package com.peekr.core.data.source.network.dto.userKeyword.request

import com.peekr.core.domain.userKeyword.model.PatchUserKeyword
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 수정 요청 바디
 *
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 */
@JsonClass(generateAdapter = true)
data class PatchUserKeywordRequest(
    val offsetX: Double,
    val offsetY: Double,
    val description: String,
)

fun PatchUserKeyword.toDataModel(): PatchUserKeywordRequest =
    PatchUserKeywordRequest(
        offsetX = offsetX,
        offsetY = offsetY,
        description = description,
    )
