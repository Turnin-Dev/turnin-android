package com.peekr.core.data.source.network.dto.user.response

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.user.model.MyProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 나의 프로필 조회 응답 바디
 *
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property friendsCount 친구 수
 * @property active 사용자 활성화 여부
 */
@JsonClass(generateAdapter = true)
data class MyProfileResponse(
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val lastLoginAt: Long,
    val friendsCount: Long,
    @Json(name = "isActive")
    val active: Boolean,
)

fun MyProfileResponse.toDomainModel(): MyProfile =
    MyProfile(
        displayId = DisplayId(displayId),
        name = Name(name),
        profileImageUrl = profileImageUrl,
        introduce = Introduce(introduce),
        lastLoginAt = lastLoginAt,
        friendsCount = friendsCount,
        active = active,
    )
