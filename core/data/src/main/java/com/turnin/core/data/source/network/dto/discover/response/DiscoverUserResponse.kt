package com.turnin.core.data.source.network.dto.discover.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.turnin.core.domain.discover.model.DiscoverUser
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId

/**
 * 탐색용 사용자 응답 바디
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 사용자 프로필 url
 */
@JsonClass(generateAdapter = true)
data class DiscoverUserResponse(
    @Json(name = "id")
    val userId: Long,
    @Json(name = "name")
    val userName: String,
    val displayId: String,
    val profileImageUrl: String?,
)

fun DiscoverUserResponse.toDomainModel(): DiscoverUser =
    DiscoverUser(
        userId = UserId(userId),
        userName = Name(userName),
        displayId = DisplayId(displayId),
        profileImageUrl = profileImageUrl,
    )
