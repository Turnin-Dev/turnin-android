package com.peekr.core.data.source.network.dto.friend.response

import com.peekr.core.domain.friend.model.Friend
import com.peekr.core.domain.friend.model.FriendId
import com.peekr.core.domain.model.FriendshipStatus
import com.peekr.core.domain.model.UserId
import com.squareup.moshi.JsonClass

/**
 * 친구 응답 바디
 *
 * @property id 친구 ID
 * @property requesterId 요청한 사용자 ID
 * @property receiverId 요청 받은 사용자 ID
 * @property status 요청 상태
 * @property respondedAt 요청 응답 일자
 * @property createdAt 요청 생성 일자
 * @property updatedAt 요청 수정 일자
 */
@JsonClass(generateAdapter = true)
data class FriendResponse(
    val id: Long,
    val requesterId: Long,
    val receiverId: Long,
    val status: FriendshipStatus,
    val respondedAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
)

fun FriendResponse.toDomainModel(): Friend =
    Friend(
        id = FriendId(id),
        requesterId = UserId(requesterId),
        receiverId = UserId(receiverId),
        status = status,
        respondedAt = respondedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
