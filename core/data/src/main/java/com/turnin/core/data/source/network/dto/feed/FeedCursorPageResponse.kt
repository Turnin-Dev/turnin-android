package com.turnin.core.data.source.network.dto.feed

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.network.util.CursorPageResponse

/**
 * 피드 커서 페이지 응답 바디
 */
@JsonClass(generateAdapter = true)
data class FeedCursorPageResponse(
    override val items: List<FeedResponse>,
    override val nextCursor: FeedCursorResponse?,
) : CursorPageResponse<FeedResponse, FeedCursorResponse>
