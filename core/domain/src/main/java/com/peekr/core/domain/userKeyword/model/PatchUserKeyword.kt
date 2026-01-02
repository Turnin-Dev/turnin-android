package com.peekr.core.domain.userKeyword.model

/**
 * 사용자 키워드 수정 요청
 *
 * @property description 키워드 설명
 */
data class PatchUserKeyword(
    val description: String,
)
