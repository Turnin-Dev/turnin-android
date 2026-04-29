package com.turnin.core.data.source.network.dto.user.request

import com.squareup.moshi.JsonClass

/**
 * FCM 토큰 요청 바디
 *
 * @property token FCM 토큰
 */
@JsonClass(generateAdapter = true)
data class FcmTokenRequest(
    val token: String,
)
