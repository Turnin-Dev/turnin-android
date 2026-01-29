package com.peekr.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peekr.core.domain.feed.model.Feed
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserInfo
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail

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
 * @property score 피드 점수(피드 표시 조건을 위한 점수, 높을수록 피드가 표시될 확률이 높음)
 * @property similarity 유사도(사용자의 키워드들과 유사한 정도를 나타냄, 1.0에 가까울수록 유사함)
 */
@Entity
data class FeedEntity(
    @PrimaryKey
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
        score = score,
        similarity = similarity,
    )
