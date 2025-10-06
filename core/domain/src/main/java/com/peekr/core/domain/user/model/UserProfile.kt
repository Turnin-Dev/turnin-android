package com.peekr.core.domain.user.model

/**
 * 사용자 프로필
 *
 * @property user [User]
 * @property friendsCount 친구 수
 */
data class UserProfile(
    val user: User,
    val friendsCount: Long,
)
