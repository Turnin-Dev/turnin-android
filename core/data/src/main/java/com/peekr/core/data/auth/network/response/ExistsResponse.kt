package com.peekr.core.data.auth.network.response

import com.squareup.moshi.JsonClass

/**
 * 존재 여부 확인 응답 바디
 *
 * @property exists 존재 여부
 */
@JsonClass(generateAdapter = true)
data class ExistsResponse(
    val exists: Boolean,
)
