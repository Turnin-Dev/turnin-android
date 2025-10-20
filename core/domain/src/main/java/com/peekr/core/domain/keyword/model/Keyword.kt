package com.peekr.core.domain.keyword.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue

/**
 * 키워드
 *
 * @property id 키워드 ID
 * @property keyword 키워드명
 * @property createdBy 키워드 최초 등록 사용자 ID
 * @property createdAt 키워드 등록 일자 (epoch)
 * @property updatedAt 키워드 수정 일자 (epoch)
 */
data class Keyword(
    val id: KeywordId,
    val keyword: KeywordValue,
    val createdBy: Long,
    val createdAt: Long,
    val updatedAt: Long,
)
