package com.peekr.core.domain.user.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name

/**
 * 사용자 수정 요청
 *
 * @property name 사용자 이름
 * @property displayId 사용자 표시 ID
 * @property oldProfileImageUrl 기존 사용자 프로필 사진 url
 * @property newProfileImageUrl 새로운 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 */
data class UserPatch(
    val name: Name,
    val displayId: DisplayId,
    val oldProfileImageUrl: String?,
    val newProfileImageUrl: String?,
    val introduce: Introduce,
)
