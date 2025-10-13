package com.peekr.domain.profile.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId

/**
 * 키워드 추가 후 결과
 *
 * @param userKeywordId 사용자 키워드 ID
 * @param keyword 키워드 ID
 * @param createBy 키워드 최초 생성자 사용자 ID
 * @param description 키워드 설명
 * @param createdAt 키워드 생성 일자
 */
data class AddKeywordResult(
    val userKeywordId: UserKeywordId,
    val keyword: KeywordId,
    val createBy: UserId,
    val description: String?,
    val createdAt: Long,
)
