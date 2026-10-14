package com.turnin.core.domain.friend.model

import com.turnin.core.domain.model.UserId

/**
 * 친구 상태 수정 모델
 *
 * @property requesterId 친구 상태 수정을 요청한 사용자 ID (나의 사용자 ID)
 * @property receiverId 친구 상태 수정을 요청 받을 사용자 ID
 * @property requestStatus 친구 상태 (친구 관계 상태랑 다름)
 */
data class PatchFriendStatus(
    val requesterId: UserId,
    val receiverId: UserId,
    val requestStatus: FriendRequestStatus,
)
