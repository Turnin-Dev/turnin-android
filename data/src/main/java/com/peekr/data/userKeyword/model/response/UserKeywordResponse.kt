package com.peekr.data.userKeyword.model.response

import com.peekr.domain.common.model.KeywordId
import com.peekr.domain.common.model.UserId
import com.peekr.domain.common.model.UserKeywordId
import com.peekr.domain.userKeyword.model.UserKeyword
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 응답 바디
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property userId 사용자 ID
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
@JsonClass(generateAdapter = true)
data class UserKeywordResponse(
    val id: Long,
    val keywordId: Long,
    val userId: Long,
    val offsetX: Double,
    val offsetY: Double,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
)

fun UserKeywordResponse.toDomainModel(): UserKeyword =
    UserKeyword(
        id = UserKeywordId(id),
        keywordId = KeywordId(keywordId),
        userId = UserId(userId),
        offsetX = offsetX,
        offsetY = offsetY,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
