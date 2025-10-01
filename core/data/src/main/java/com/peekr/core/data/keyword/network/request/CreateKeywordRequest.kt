package com.peekr.core.data.keyword.network.request

import com.squareup.moshi.JsonClass

/**
 * 키워드 생성 요청 바디
 *
 * @property keyword 키워드명
 */
@JsonClass(generateAdapter = true)
data class CreateKeywordRequest(
    val keyword: String,
)
