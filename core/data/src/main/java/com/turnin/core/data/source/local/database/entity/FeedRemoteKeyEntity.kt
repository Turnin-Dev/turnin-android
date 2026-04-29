package com.turnin.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 피드 페이지네이션 조회 사용할 커서 엔티티
 *
 * @param id 고정 ID
 * @param cursorScore 커서 값 1 (피드 점수)
 * @param cursorCreatedAt 커서 값 2 (생성 일자)
 * @param cursorUserKeywordId 커서 값 3 (사용자 키워드 ID)
 */
@Entity
data class FeedRemoteKeyEntity(
    @PrimaryKey
    val id: String = SINGLE_CURSOR_ID,
    val cursorScore: Double,
    val cursorCreatedAt: Long,
    val cursorUserKeywordId: Long,
) {
    companion object {
        const val SINGLE_CURSOR_ID = "SINGLE_CURSOR_ID"
    }
}
