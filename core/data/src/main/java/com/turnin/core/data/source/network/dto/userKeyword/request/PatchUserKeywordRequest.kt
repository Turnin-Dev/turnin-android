package com.turnin.core.data.source.network.dto.userKeyword.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.userKeyword.model.PatchUserKeyword

/**
 * 사용자 키워드 수정 요청 바디
 */
@JsonClass(generateAdapter = true)
data class PatchUserKeywordRequest(
    val userKeywordId: Long,
    val keywordName: String,
    val description: String,
)

fun PatchUserKeyword.toDataModel(): PatchUserKeywordRequest =
    PatchUserKeywordRequest(
        userKeywordId = userKeywordId.value,
        keywordName = keywordName.value,
        description = description.value,
    )
