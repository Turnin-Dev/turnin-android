package com.peekr.core.data.userKeyword.network.response

import com.peekr.core.domain.model.KeywordDescription
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 설명 응답 바디
 */
@JsonClass(generateAdapter = true)
data class DescriptionResponse(
    val description: String,
)

fun DescriptionResponse.toDomainModel(): KeywordDescription =
    KeywordDescription(this.description)
