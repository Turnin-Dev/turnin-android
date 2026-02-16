package com.peekr.core.data.source.network.dto.block.response

import com.peekr.core.domain.block.model.BlockUser
import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.squareup.moshi.JsonClass

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
data class BlockUserResponse(
    val id: Long,
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
)

fun BlockUserResponse.toDomainModel(): BlockUser =
    BlockUser(
        id = BlockId(id),
        userId = UserId(userId),
        displayId = DisplayId(displayId),
        name = Name(name),
        profileImageUrl = profileImageUrl,
    )
