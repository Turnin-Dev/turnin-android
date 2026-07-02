package com.turnin.core.domain.friend.model

import com.turnin.core.domain.model.UserId

/**
 * 친구 삭제 모델
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 */
data class DeleteFriend(
    val requesterId: UserId,
    val receiverId: UserId,
)
