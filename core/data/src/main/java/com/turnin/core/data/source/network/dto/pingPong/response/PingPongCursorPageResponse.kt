package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.network.util.CursorPageResponse

/**
 * 핑퐁 질문/답변 커서 페이지 응답 바디
 */
@JsonClass(generateAdapter = true)
data class PingPongCursorPageResponse(
    override val items: List<PingPongDetailResponse>,
    override val nextCursor: Long?,
) : CursorPageResponse<PingPongDetailResponse, Long>
