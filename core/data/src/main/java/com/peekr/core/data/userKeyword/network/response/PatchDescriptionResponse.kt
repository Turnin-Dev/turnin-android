package com.peekr.core.data.userKeyword.network.response

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 설명 수정 요청 바디
 */
@JsonClass(generateAdapter = true)
data class PatchDescriptionResponse(
    val description: String,
)

fun PatchDescriptionResponse.toDomainModel(): PatchDescription =
    PatchDescription(KeywordDescription(description))
