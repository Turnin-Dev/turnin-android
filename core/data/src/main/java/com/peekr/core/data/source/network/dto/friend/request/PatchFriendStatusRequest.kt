package com.peekr.core.data.source.network.dto.friend.request

import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.squareup.moshi.JsonClass

/**
 * 친구 상태 수정 요청 바디
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 * @property status 친구 상태
 */
@JsonClass(generateAdapter = true)
data class PatchFriendStatusRequest(
    val requesterId: Long,
    val receiverId: Long,
    val status: FriendStatus,
)

fun PatchFriendStatus.toDataModel(): PatchFriendStatusRequest =
    PatchFriendStatusRequest(
        requesterId = requesterId.value,
        receiverId = receiverId.value,
        status = status,
    )
