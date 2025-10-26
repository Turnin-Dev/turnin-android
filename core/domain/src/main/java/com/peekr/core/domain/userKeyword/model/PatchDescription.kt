package com.peekr.core.domain.userKeyword.model

import com.peekr.core.domain.model.KeywordDescription

/**
 * 사용자 키워드 설명 수정 모델
 *
 * @property description 설명
 */
data class PatchDescription(
    val description: KeywordDescription,
)
