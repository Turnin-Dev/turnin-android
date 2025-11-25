package com.peekr.core.data.source.network.dto.userKeyword.response

import com.peekr.core.domain.userKeyword.model.PatchOffset
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 오프셋 수정 응답 바디
 */
@JsonClass(generateAdapter = true)
data class PatchOffsetResponse(
    val offsetX: Float,
    val offsetY: Float,
)

fun PatchOffsetResponse.toDomainModel(): PatchOffset =
    PatchOffset(offsetX.toDouble(), offsetY.toDouble())
