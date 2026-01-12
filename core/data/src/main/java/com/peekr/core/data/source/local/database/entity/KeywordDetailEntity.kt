package com.peekr.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 키워드 상세 정보 엔티티
 *
 * @property id 키워드 디테일 ID
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property keyword 키워드
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 */
@Entity
data class KeywordDetailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userKeywordId: Long,
    val keywordId: Long,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val keyword: String,
    val description: String,
    val createdAt: Long,
)
