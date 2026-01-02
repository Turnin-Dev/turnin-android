package com.peekr.core.data.source.network.dto.userKeyword.request

import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 생성 요청 바디
 *
 * @property userId 사용자 ID
 * @property keyword 키워드 명
 * @property description 키워드 설명
 */
@JsonClass(generateAdapter = true)
data class CreateUserKeywordRequest(
    val userId: Long,
    @Json(name = "keywordName")
    val keyword: String,
    val description: String,
)

fun CreateUserKeyword.toDataModel(): CreateUserKeywordRequest =
    CreateUserKeywordRequest(
        userId = userId.value,
        keyword = keyword.value,
        description = description.value,
    )
