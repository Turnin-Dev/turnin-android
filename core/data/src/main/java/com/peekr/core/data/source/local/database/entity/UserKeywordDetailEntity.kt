package com.peekr.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserInfo
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail

/**
 * 사용자 키워드 상세 정보 엔티티 (페이징 용도)
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 url
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
@Entity
data class UserKeywordDetailEntity(
    @PrimaryKey
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
    val description: String,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

fun UserKeywordDetailEntity.toDomainModel(): UserKeywordDetail =
    UserKeywordDetail(
        userKeywordId = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keywordName = KeywordName(keywordName),
        description = KeywordDescription(description),
        userInfo = UserInfo(
            userId = UserId(userId),
            userName = Name(userName),
            profileImageUrl = profileImageUrl,
        ),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
