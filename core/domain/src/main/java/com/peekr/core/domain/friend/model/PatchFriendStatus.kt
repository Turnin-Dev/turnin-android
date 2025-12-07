package com.peekr.core.domain.friend.model

import com.peekr.core.domain.model.UserId

/**
 * 친구 관계 상태 수정 모델
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 * @property status 친구 상태 (친구 관계 상태랑 다름)
 */
data class PatchFriendStatus(
    val requesterId: UserId,
    val receiverId: UserId,
    val status: FriendStatus,
)
