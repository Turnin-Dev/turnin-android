package com.turnin.core.data.source.network.dto.notification.response

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.network.util.CursorPageResponse

/**
 * 알림 목록 응답 바디
 *
 * @property items 알림 목록
 * @property nextCursor 다음 커서
 */
@JsonClass(generateAdapter = true)
data class NotificationCursorPageResponse(
    override val items: List<NotificationResponse>,
    override val nextCursor: Long?,
) : CursorPageResponse<NotificationResponse, Long>
