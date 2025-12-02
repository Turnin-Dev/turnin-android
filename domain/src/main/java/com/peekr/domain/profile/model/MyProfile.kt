package com.peekr.domain.profile.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.userKeyword.model.UserKeyword

/**
 * 나의 프로필
 *
 * @property displayId 사용자 표시 ID
 * @property name 이름
 * @property profileImageUrl 프로필 사진 url
 * @property introduce 소개 글
 * @property friendsCount 친구 수
 * @property lastLoginAt 마지막 로그인 일시
 * @property keywords 키워드 리스트
 * @property active 사용자 활성화 여부
 */
data class MyProfile(
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val keywords: List<UserKeyword>,
    val active: Boolean,
)
