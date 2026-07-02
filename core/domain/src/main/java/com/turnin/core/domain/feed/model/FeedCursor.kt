package com.turnin.core.domain.feed.model

import com.turnin.core.domain.model.UserKeywordId

/**
 * 피드 커서
 *
 * @property score 피드 점수(피드 표시 조건을 위한 점수, 높을수록 피드가 표시될 확률이 높음)
 * @property userKeywordId 사용자 키워드 ID
 */
data class FeedCursor(
    val score: Double,
    val userKeywordId: UserKeywordId,
)
