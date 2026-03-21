package com.peekr.data.notification.dto

import com.squareup.moshi.JsonClass

/**
 * FCM 토큰 응답 바디
 *
 * @property id FCM 토큰 ID
 * @property userId 사용자 ID
 * @property token FCM 토큰
 * @property isActive 활성화 여부
 */
@JsonClass(generateAdapter = true)
data class FcmTokenResponse(
    val id: Long,
    val userId: Long,
    val token: String,
    val isActive: Boolean,
)
