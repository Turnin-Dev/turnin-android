package com.turnin.core.data.source.network.dto.block.response

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.network.util.CursorPageResponse

/**
 * 차단 목록 응답 바디
 *
 * @property items 차단 목록
 * @property nextCursor 다음 커서
 */
@JsonClass(generateAdapter = true)
data class BlockedUserCursorPageResponse(
    override val items: List<BlockedUserResponse>,
    override val nextCursor: Long?,
) : CursorPageResponse<BlockedUserResponse, Long>
