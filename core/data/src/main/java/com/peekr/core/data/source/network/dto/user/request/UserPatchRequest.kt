package com.peekr.core.data.source.network.dto.user.request

import com.peekr.core.domain.user.model.UserPatch
import com.squareup.moshi.JsonClass

/**
 * 사용자 수정 요청 바디
 *
 * @property name 사용자 이름
 * @property displayId 사용자 표시 ID
 * @property oldProfileImageUrl 기존 사용자 프로필 사진 url
 * @property newProfileImageUrl 새로운 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 */
@JsonClass(generateAdapter = true)
data class UserPatchRequest(
    val name: String,
    val displayId: String,
    val oldProfileImageUrl: String?,
    val newProfileImageUrl: String?,
    val introduce: String,
)

fun UserPatch.toDataModel(): UserPatchRequest =
    UserPatchRequest(
        name = name.value,
        displayId = displayId.value,
        oldProfileImageUrl = oldProfileImageUrl,
        newProfileImageUrl = newProfileImageUrl,
        introduce = introduce.value,
    )
