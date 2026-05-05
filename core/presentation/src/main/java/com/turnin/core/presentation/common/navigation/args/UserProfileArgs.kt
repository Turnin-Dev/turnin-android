package com.turnin.core.presentation.common.navigation.args

/**
 * 사용자 프로필 네비게이션 인자 값
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property displayId 사용자 표시 ID
 * @property profileImageUrl 프로필 사진 URL
 * @property blockId 차단 ID
 * @property forceRefresh 강제 새로고침 여부
 */
data class UserProfileArgs(
    val userId: Long,
    val userName: String? = null,
    val displayId: String? = null,
    val profileImageUrl: String? = null,
    val blockId: Long? = null,
    val forceRefresh: Boolean = false,
)
