package com.peekr.core.data.user.network.request

import com.peekr.core.domain.user.model.UserPatch
import com.squareup.moshi.JsonClass

/**
 * 사용자 수정 요청 바디
 *
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 */
@JsonClass(generateAdapter = true)
data class UserPatchRequest(
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
)

fun UserPatch.toDataModel(): UserPatchRequest =
    UserPatchRequest(
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
    )
