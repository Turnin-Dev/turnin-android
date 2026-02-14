package com.peekr.core.data.source.network.dto.block.response

import com.squareup.moshi.JsonClass

/**
 * 차단 응답 바디
 *
 * @property id 차단 ID
 * @property blockerId 차단 요청한 사용자 ID
 * @property blockedId 차단 당한 사용자 ID
 * @property reasonId 차단 사유 ID
 * @property customReason 차단 기타 사유
 */
@JsonClass(generateAdapter = true)
data class BlockResponse(
    val id: Long,
    val blockerId: Long,
    val blockedId: Long,
    val reasonId: Long,
    val customReason: String?,
)
