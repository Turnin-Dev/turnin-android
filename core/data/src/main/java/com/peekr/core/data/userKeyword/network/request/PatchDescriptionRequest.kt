package com.peekr.core.data.userKeyword.network.request

import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 설명 수정 요청 바디
 */
@JsonClass(generateAdapter = true)
data class PatchDescriptionRequest(
    val description: String?,
)

fun PatchDescription.toDataModel(): PatchDescriptionRequest =
    PatchDescriptionRequest(description.value)
