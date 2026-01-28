package com.peekr.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail

/**
 * 나의 키워드 상세 정보 엔티티
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
@Entity
data class MyKeywordEntity(
    @PrimaryKey
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
)

fun MyKeywordEntity.toUserKeywordDetail(): UserKeyword =
    UserKeyword(
        id = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keyword = KeywordName(keywordName),
        description = KeywordDescription(description),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun UserKeywordDetail.toEntity(): MyKeywordEntity =
    MyKeywordEntity(
        userKeywordId = userKeywordId.value,
        keywordId = keywordId.value,
        keywordName = keywordName.value,
        description = description.value,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
