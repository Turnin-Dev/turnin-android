package com.turnin.core.data.source.network.dto.discover.response

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.network.util.CursorPageResponse

/**
 * 탐색 컨텍스트 커서 페이지 응답 바디
 */
@JsonClass(generateAdapter = true)
data class DiscoverContextCursorPageResponse(
    override val items: List<DiscoverContextResponse>,
    override val nextCursor: String?,
) : CursorPageResponse<DiscoverContextResponse, String>
