package com.peekr.data.notification.dto

import com.peekr.core.data.source.network.util.CursorPageResponse
import com.squareup.moshi.JsonClass

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
