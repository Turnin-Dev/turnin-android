package com.turnin.core.data.source.network.dto.block.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.block.model.BlockedUser
import com.turnin.core.domain.model.BlockId
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId

/**
 * 차단 사용자 응답 바디
 *
 * @property id 차단 ID
 * @property userId 차단한 사용자 ID
 * @property displayId 차단한 사용자 표시 ID
 * @property name 차단한 사용자 명
 * @property profileImageUrl 차단한 사용자 프로필 사진 url
 */
@JsonClass(generateAdapter = true)
data class BlockedUserResponse(
    val id: Long,
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
)

fun BlockedUserResponse.toDomainModel(): BlockedUser =
    BlockedUser(
        id = BlockId(id),
        userId = UserId(userId),
        displayId = DisplayId(displayId),
        name = Name(name),
        profileImageUrl = profileImageUrl,
    )
