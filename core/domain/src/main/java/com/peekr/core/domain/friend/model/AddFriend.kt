package com.peekr.core.domain.friend.model

import com.peekr.core.domain.model.UserId

/**
 * 친구 추가 모델
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 */
data class AddFriend(
    val requesterId: UserId,
    val receiverId: UserId,
)
