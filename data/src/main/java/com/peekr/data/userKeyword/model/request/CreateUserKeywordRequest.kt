package com.peekr.data.userKeyword.model.request

import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 생성 요청 바디
 *
 * @property userId 사용자 ID
 * @property keywordId 키워드 ID
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 */
@JsonClass(generateAdapter = true)
data class CreateUserKeywordRequest(
    val userId: Long,
    val keywordId: Long,
    val offsetX: Double,
    val offsetY: Double,
    val description: String,
)
