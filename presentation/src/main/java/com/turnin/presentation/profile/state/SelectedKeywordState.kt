package com.turnin.presentation.profile.state

import com.turnin.core.domain.model.UserKeywordId

/**
 * 선택된 키워드 상태
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keyword 키워드 명
 */
data class SelectedKeywordState(
    val userKeywordId: UserKeywordId? = null,
    val keyword: String? = null,
)
