package com.peekr.core.domain.userKeyword.model

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserKeywordId

/**
 * 사용자 키워드 수정 요청 모델
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 */
data class PatchUserKeyword(
    val userKeywordId: UserKeywordId,
    val keywordName: KeywordName,
    val description: KeywordDescription,
)
