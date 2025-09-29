package com.peekr.presentation.profile.state

import com.peekr.core.presentation.keyword.model.UiKeyword
import com.peekr.core.presentation.util.UiText

/**
 * 프로필 상태 클래스
 *
 * @param displayId 사용자 표시 ID
 * @param name 사용자 이름
 * @param profileImageUrl 프로필 사진 url
 * @param friendsTotal 친구 수
 * @param introduce 소개 글
 * @param keywords 키워드 리스트
 * @param loading 로딩 여부
 * @param error 에러 메시지
 */
data class ProfileState(
    val displayId: String = "",
    val name: String = "",
    val profileImageUrl: String? = null,
    val friendsTotal: Long = 0L,
    val introduce: String = "",
    val keywords: List<UiKeyword> = emptyList(),
    val loading: Boolean = false,
    val error: UiText? = null,
)
