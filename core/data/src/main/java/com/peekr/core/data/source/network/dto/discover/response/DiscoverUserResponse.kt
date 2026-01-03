package com.peekr.core.data.source.network.dto.discover.response

import com.peekr.core.domain.discover.model.DiscoverUser
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.squareup.moshi.JsonClass

/**
 * 탐색용 사용자 응답 바디
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 사용자 프로필 url
 */
@JsonClass(generateAdapter = true)
data class DiscoverUserResponse(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
)

fun DiscoverUserResponse.toDomainModel(): DiscoverUser =
    DiscoverUser(
        userId = UserId(userId),
        userName = Name(userName),
        profileImageUrl = profileImageUrl,
    )
