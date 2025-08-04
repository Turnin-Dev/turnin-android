package com.peekr.data.account.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 사용자 존재 여부 확인 응답 바디
 *
 * @property isExists 사용자 존재 여부
 */
@JsonClass(generateAdapter = true)
data class ExistsUserResponse(
    @Json(name = "isExist") val isExists: Boolean,
)
