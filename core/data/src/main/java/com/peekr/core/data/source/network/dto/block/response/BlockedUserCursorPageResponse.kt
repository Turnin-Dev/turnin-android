package com.peekr.core.data.source.network.dto.block.response

import com.peekr.core.data.source.network.util.CursorPageResponse
import com.squareup.moshi.JsonClass

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
