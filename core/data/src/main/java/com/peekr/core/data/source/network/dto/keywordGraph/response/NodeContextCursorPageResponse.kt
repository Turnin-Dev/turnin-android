package com.peekr.core.data.source.network.dto.keywordGraph.response

import com.peekr.core.data.source.network.util.CursorPageResponse
import com.squareup.moshi.JsonClass

/**
 * 노드 컨텍스트 응답 바디
 */
@JsonClass(generateAdapter = true)
data class NodeContextCursorPageResponse(
    override val items: List<NodeContextResponse>,
    override val nextCursor: Long?,
) : CursorPageResponse<NodeContextResponse, Long>
