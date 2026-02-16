package com.peekr.core.data.source.network.dto.block.request

import com.peekr.core.domain.block.model.CreateBlock
import com.squareup.moshi.JsonClass

/**
 * 차단 요청 바디
 *
 * @property blockerId 차단 요청한 사용자 ID
 * @property blockedId 차단 당한 사용자 ID
 * @property reasonId 차단 사유 ID
 * @property customReason 차단 기타 사유
 */
@JsonClass(generateAdapter = true)
data class BlockRequest(
    val blockerId: Long,
    val blockedId: Long,
    val reasonId: Long,
    val customReason: String?,
)

fun CreateBlock.toDataModel(): BlockRequest =
    BlockRequest(
        blockerId = blockerId.value,
        blockedId = blockedId.value,
        reasonId = reasonId.value,
        customReason = customReason,
    )
