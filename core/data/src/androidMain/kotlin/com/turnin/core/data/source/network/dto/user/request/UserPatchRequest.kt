package com.turnin.core.data.source.network.dto.user.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.user.model.ProfileImagePatch
import com.turnin.core.domain.user.model.UserPatch

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
        newProfileImageUrl = when (val patch = profileImagePatch) {
            ProfileImagePatch.Unchanged -> oldProfileImageUrl
            ProfileImagePatch.Remove -> null
            is ProfileImagePatch.Update -> patch.url
        },
        introduce = introduce.value,
    )
