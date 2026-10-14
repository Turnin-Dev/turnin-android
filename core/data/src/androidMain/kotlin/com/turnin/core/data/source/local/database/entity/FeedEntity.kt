package com.turnin.core.data.source.local.database.entity

import androidx.room.Entity
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedType
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.userKeyword.model.UserInfo
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail

/**
 * 피드 엔티티
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 * @property sortOrder 피드 정렬 순서
 */
@Entity(primaryKeys = ["type", "userKeywordId"])
data class FeedEntity(
    val type: FeedType,
    val userKeywordId: Long,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val keywordId: Long,
    val keyword: String,
    val description: String,
    val createdAt: Long,
    val sortOrder: Int,
)

// TODO: 서버에서 updatedAt 제공 시 교체 예정
fun FeedEntity.toUserKeywordDetail(): UserKeywordDetail =
    UserKeywordDetail(
        userKeywordId = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keywordName = KeywordName(keyword),
        description = KeywordDescription(description),
        userInfo = UserInfo(
            userId = UserId(userId),
            userName = Name(userName),
            profileImageUrl = profileImageUrl,
        ),
        createdAt = createdAt,
        updatedAt = createdAt,
    )

fun FeedEntity.toDomainModel(): Feed =
    Feed(
        userKeywordId = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keyword = KeywordName(keyword),
        description = KeywordDescription(description),
        userId = UserId(userId),
        userName = Name(userName),
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        sortOrder = sortOrder,
    )
