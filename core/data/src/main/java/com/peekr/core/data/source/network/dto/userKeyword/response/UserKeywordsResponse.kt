package com.peekr.core.data.source.network.dto.userKeyword.response

import com.peekr.core.domain.userKeyword.model.UserKeywords
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 리스트 응답 바디
 *
 * @property keywords 사용자 키워드 리스트
 */
@JsonClass(generateAdapter = true)
data class UserKeywordsResponse(
    val keywords: List<UserKeywordResponse>,
)

fun UserKeywordsResponse.toDomainModel(): UserKeywords =
    UserKeywords(
        keywords = this.keywords.map { it.toDomainModel() },
    )
