package com.peekr.core.data.source.network.dto.discover.response

import com.peekr.core.domain.discover.model.DiscoverKeyword
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserKeywordId
import com.squareup.moshi.JsonClass

/**
 * 탐색용 키워드 응답 바디
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 */
@JsonClass(generateAdapter = true)
data class DiscoverKeywordResponse(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
)

fun DiscoverKeywordResponse.toDomainModel(): DiscoverKeyword =
    DiscoverKeyword(
        userKeywordId = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keywordName = KeywordName(keywordName),
    )
