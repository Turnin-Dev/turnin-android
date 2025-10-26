package com.peekr.core.data.userKeyword.network.request

import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 오프셋 수정 요청 바디
 */
@JsonClass(generateAdapter = true)
data class PatchOffsetRequest(
    val offsetX: Float,
    val offsetY: Float,
)

fun PatchOffset.toDataModel(): PatchOffsetRequest =
    PatchOffsetRequest(offsetX.toFloat(), offsetY.toFloat())
