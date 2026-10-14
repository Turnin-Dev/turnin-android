package com.turnin.core.data.source.network.dto.feed

import com.squareup.moshi.JsonClass
import com.turnin.core.data.source.local.database.entity.FeedEntity
import com.turnin.core.domain.feed.model.FeedType

/**
 * 피드 응답 바디
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 */
@JsonClass(generateAdapter = true)
data class FeedResponse(
    val userKeywordId: Long,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val keywordId: Long,
    val keyword: String,
    val description: String,
    val createdAt: Long,
)

fun FeedResponse.toEntity(type: FeedType, sortOrder: Int): FeedEntity =
    FeedEntity(
        type = type,
        userKeywordId = userKeywordId,
        keywordId = keywordId,
        keyword = keyword,
        description = description,
        userId = userId,
        userName = userName,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        sortOrder = sortOrder,
    )
