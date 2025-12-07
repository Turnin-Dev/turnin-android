package com.peekr.core.domain.user.model

import com.peekr.core.domain.friend.model.FriendshipStatus
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId

/**
 * 사용자 프로필
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property friendsCount 친구 수
 * @property friendshipStatus 친구 관계 상태
 * @property active 사용자 활성화 여부
 */
data class CoreUserProfile(
    val userId: UserId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val lastLoginAt: Long,
    val friendsCount: Long,
    val friendshipStatus: FriendshipStatus,
    val active: Boolean,
)
