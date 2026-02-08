package com.peekr.core.data.source.network.dto.friend.response

import com.peekr.core.domain.friend.model.FriendId
import com.peekr.core.domain.friend.model.FriendRequestStatus
import com.peekr.core.domain.model.UserId

/**
 * 나에게 들어온 친구 요청
 *
 * @property id 친구 ID
 * @property requesterId 요청한 사용자 ID
 * @property requestStatus 요청 상태
 * @property respondedAt 요청 응답 일자
 * @property createdAt 요청 생성 일자
 * @property updatedAt 요청 수정 일자
 */
data class IncomingRequestResponse(
    val id: FriendId,
    val requesterId: UserId,
    val requestStatus: FriendRequestStatus,
    val respondedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)
