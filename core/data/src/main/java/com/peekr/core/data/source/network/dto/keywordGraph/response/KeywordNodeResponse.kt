package com.peekr.core.data.source.network.dto.keywordGraph.response

import com.squareup.moshi.JsonClass

/**
 * 키워드 노드 응답 바디
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 */
@JsonClass(generateAdapter = true)
data class KeywordNodeResponse(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
)
