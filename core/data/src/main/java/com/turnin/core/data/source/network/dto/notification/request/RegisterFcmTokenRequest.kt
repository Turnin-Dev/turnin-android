package com.turnin.core.data.source.network.dto.notification.request

import com.squareup.moshi.JsonClass

/**
 * FCM 토큰 등록 요청 바디
 *
 * @property token FCM 토큰
 */
@JsonClass(generateAdapter = true)
data class RegisterFcmTokenRequest(
    val token: String,
)
