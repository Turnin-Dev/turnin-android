package com.turnin.core.data.source.network.dto.user.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreUserProfile

/**
 * 사용자 프로필 조회 응답 바디
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property friendsCount 친구 수
 * @property friendStatus 친구 관계 상태
 * @property active 사용자 활성화 여부
 * @property isBlocked 차단 여부
 */
@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val lastLoginAt: Long,
    val friendsCount: Long,
    val friendStatus: FriendStatus,
    @Json(name = "isActive")
    val active: Boolean,
    val isBlocked: Boolean,
)

fun UserProfileResponse.toDomainModel(): CoreUserProfile =
    CoreUserProfile(
        userId = UserId(userId),
        displayId = DisplayId(displayId),
        name = Name(name),
        profileImageUrl = profileImageUrl,
        introduce = Introduce(introduce),
        lastLoginAt = lastLoginAt,
        friendsCount = friendsCount,
        friendStatus = friendStatus,
        active = active,
        isBlocked = isBlocked,
    )
