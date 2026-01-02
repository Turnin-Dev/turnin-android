package com.peekr.core.data.source.network.dto.userKeyword.request

import com.peekr.core.domain.userKeyword.model.PatchUserKeyword
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 수정 요청 바디
 *
 * @property description 키워드 설명
 */
@JsonClass(generateAdapter = true)
data class PatchUserKeywordRequest(
    val description: String,
)

fun PatchUserKeyword.toDataModel(): PatchUserKeywordRequest =
    PatchUserKeywordRequest(
        description = description,
    )
