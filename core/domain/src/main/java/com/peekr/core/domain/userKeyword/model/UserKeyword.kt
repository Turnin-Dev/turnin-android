package com.peekr.core.domain.userKeyword.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId

/**
 * 사용자 키워드
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property userId 사용자 ID
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UserKeyword(
    val id: UserKeywordId,
    val keywordId: KeywordId,
    val keywordName: String,
    val userId: UserId,
    val offsetX: Double,
    val offsetY: Double,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
