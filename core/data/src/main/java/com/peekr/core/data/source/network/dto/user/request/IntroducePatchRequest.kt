package com.peekr.core.data.source.network.dto.user.request

import com.squareup.moshi.JsonClass

/**
 * 사용자 소개글 수정 요청 바디
 *
 * @property introduce 사용자 소개 글
 */
@JsonClass(generateAdapter = true)
data class IntroducePatchRequest(
    val introduce: String,
)
