package com.peekr.core.domain.user.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.Role
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId

/**
 * 사용자
 *
 * @property id 사용자 ID
 * @property role 사용자 역할
 * @property provider 사용자가 로그인한 로그인 제공업체
 * @property providerId 로그인 제공업체 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property active 사용자 활성화 여부
 */
data class User(
    val id: UserId,
    val role: Role,
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val lastLoginAt: Long,
    val active: Boolean,
)
