package com.peekr.core.data.source.network.dto.userKeyword.response

import com.peekr.core.data.source.local.database.entity.MyKeywordDetailEntity
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 응답 바디
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
@JsonClass(generateAdapter = true)
data class UserKeywordResponse(
    val id: Long,
    val keywordId: Long,
    @Json(name = "keywordName")
    val keyword: String,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

// TODO: 해당 메서드에서 사용하는 VO 객체에서 유효성 검사 예외를 던질 때 처리 가능한지 테스트 해보기
fun UserKeywordResponse.toDomainModel(): UserKeyword =
    UserKeyword(
        id = UserKeywordId(id),
        keywordId = KeywordId(keywordId),
        keyword = KeywordName(keyword),
        description = KeywordDescription(description ?: ""),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun UserKeywordResponse.toEntity(): MyKeywordDetailEntity =
    MyKeywordDetailEntity(
        userKeywordId = id,
        keywordId = keywordId,
        keywordName = keyword,
        description = description ?: "",
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
