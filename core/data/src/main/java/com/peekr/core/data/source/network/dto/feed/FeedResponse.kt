package com.peekr.core.data.source.network.dto.feed

import com.peekr.core.data.source.local.database.entity.FeedEntity
import com.squareup.moshi.JsonClass

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
 * @property score 피드 점수(피드 표시 조건을 위한 점수, 높을수록 피드가 표시될 확률이 높음)
 * @property similarity 유사도(사용자의 키워드들과 유사한 정도를 나타냄, 1.0에 가까울수록 유사함)
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
    val score: Double,
    val similarity: Double,
)

fun FeedResponse.toEntity(): FeedEntity =
    FeedEntity(
        userKeywordId = userKeywordId,
        keywordId = keywordId,
        keyword = keyword,
        description = description,
        userId = userId,
        userName = userName,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        score = score,
        similarity = similarity,
    )
