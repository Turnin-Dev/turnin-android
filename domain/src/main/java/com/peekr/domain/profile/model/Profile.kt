package com.peekr.domain.profile.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.userKeyword.model.UserKeyword

/**
 * 사용자 프로필
 *
 * @property displayId 사용자 표시 ID
 * @property name 이름
 * @property friendsTotal 친구 수
 * @property profileImageUrl 프로필 사진 url
 * @property introduce 소개 글
 * @property keywords 키워드 리스트
 */
data class Profile(
    val displayId: DisplayId,
    val name: Name,
    val friendsTotal: Long,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val keywords: List<UserKeyword>,
)
