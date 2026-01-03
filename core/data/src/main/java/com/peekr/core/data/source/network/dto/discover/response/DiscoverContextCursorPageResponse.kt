package com.peekr.core.data.source.network.dto.discover.response

import com.peekr.core.data.source.network.util.CursorPageResponse
import com.squareup.moshi.JsonClass

/**
 * 탐색 컨텍스트 커서 페이지 응답 바디
 */
@JsonClass(generateAdapter = true)
data class DiscoverContextCursorPageResponse(
    override val items: List<DiscoverContextResponse>,
    override val nextCursor: Long?,
) : CursorPageResponse<DiscoverContextResponse, Long>
