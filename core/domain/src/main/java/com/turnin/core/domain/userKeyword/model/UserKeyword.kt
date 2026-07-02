package com.turnin.core.domain.userKeyword.model

import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.UserKeywordId

/**
 * 사용자 키워드
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property description 키워드 설명
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UserKeyword(
    val id: UserKeywordId,
    val keywordId: KeywordId,
    val keyword: KeywordName,
    val description: KeywordDescription,
    val createdAt: Long,
    val updatedAt: Long,
)
