package com.turnin.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turnin.core.domain.feed.model.FeedType

/**
 * 피드 페이지네이션 조회 사용할 커서 엔티티
 *
 * @param type 피드 타입
 * @param cursor 커서
 */
@Entity
data class FeedRemoteKeyEntity(
    @PrimaryKey
    val type: FeedType,
    val cursor: String,
)
