package com.turnin.core.data.source.network.dto.block.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.block.model.BlockReason
import com.turnin.core.domain.block.model.BlockReasonId

/**
 * 차단 사유 응답 바디
 *
 * @property id 차단 사유 ID
 * @property code 차단 사유 코드
 * @property description 차단 사유 설명
 */
@JsonClass(generateAdapter = true)
data class BlockReasonResponse(
    val id: Long,
    val code: String,
    val description: String,
)

fun BlockReasonResponse.toDomainModel(): BlockReason =
    BlockReason(
        id = BlockReasonId(id),
        code = code,
        description = description,
    )
