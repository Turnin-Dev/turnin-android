package com.turnin.core.data.source.network.dto.friend.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.friend.model.AddFriend

/**
 * 친구 추가 요청 바디
 *
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 */
@JsonClass(generateAdapter = true)
data class AddFriendRequest(
    val requesterId: Long,
    val receiverId: Long,
)

fun AddFriend.toDataModel(): AddFriendRequest =
    AddFriendRequest(
        requesterId = requesterId.value,
        receiverId = receiverId.value,
    )
