package com.turnin.core.data.source.network.dto.keyword.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.keyword.model.Keyword
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName

/**
 * 키워드 조회 응답 바디
 *
 * @property id 키워드 ID
 * @property keyword 키워드명
 * @property createdBy 키워드 최초 등록 사용자 ID
 * @property createdAt 키워드 등록일자 (epoch)
 * @property updatedAt 키워드 수정일자 (epoch)
 */
@JsonClass(generateAdapter = true)
data class KeywordResponse(
    val id: Long,
    val keyword: String,
    val createdBy: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)

fun KeywordResponse.toDomainModel(): Keyword =
    Keyword(
        id = KeywordId(id),
        keyword = KeywordName(keyword),
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
