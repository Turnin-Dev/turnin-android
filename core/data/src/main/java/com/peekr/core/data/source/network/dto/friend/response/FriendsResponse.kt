package com.peekr.core.data.source.network.dto.friend.response

import com.squareup.moshi.JsonClass

/**
 * 친구 목록 응답 바디
 *
 * @property friends 친구 목록
 */
@JsonClass(generateAdapter = true)
data class FriendsResponse(
    val friends: List<FriendResponse>,
)
