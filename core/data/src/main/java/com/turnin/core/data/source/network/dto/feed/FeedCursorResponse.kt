package com.turnin.core.data.source.network.dto.feed

import com.squareup.moshi.JsonClass

/**
 * 피드 커서 응답 바디
 *
 * @property score 피드 점수(피드 표시 조건을 위한 점수, 높을수록 피드가 표시될 확률이 높음)
 * @property userKeywordId 사용자 키워드 ID
 */
@JsonClass(generateAdapter = true)
data class FeedCursorResponse(
    val score: Double,
    val userKeywordId: Long,
)
