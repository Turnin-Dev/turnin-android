package com.turnin.core.data.source.network.dto.friend.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.friend.model.DeleteFriend

/**
 * 친구 삭제 요청 바디
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 */
@JsonClass(generateAdapter = true)
data class DeleteFriendRequest(
    val requesterId: Long,
    val receiverId: Long,
)

fun DeleteFriend.toDataModel(): DeleteFriendRequest =
    DeleteFriendRequest(
        requesterId = requesterId.value,
        receiverId = receiverId.value,
    )
