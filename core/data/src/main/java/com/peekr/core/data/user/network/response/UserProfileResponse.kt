package com.peekr.core.data.user.network.response

import com.peekr.core.domain.user.model.DisplayId
import com.peekr.core.domain.user.model.Introduce
import com.peekr.core.domain.user.model.Name
import com.peekr.core.domain.user.model.ProviderId
import com.peekr.core.domain.user.model.Role
import com.peekr.core.domain.user.model.SocialLoginProvider
import com.peekr.core.domain.user.model.User
import com.peekr.core.domain.user.model.UserId
import com.peekr.core.domain.user.model.UserProfile
import com.squareup.moshi.JsonClass

/**
 * 사용자 프로필 조회 응답 바디
 *
 * @property id 사용자 ID
 * @property role 사용자 역할
 * @property provider 사용자가 로그인한 로그인 제공업체
 * @property providerId 로그인 제공업체 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property active 사용자 활성화 여부
 */
@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    val id: Long,
    val role: Role,
    val provider: SocialLoginProvider,
    val providerId: String,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val lastLoginAt: Long,
    val active: Boolean,
    val friendsCount: Long,
)

fun UserProfileResponse.toDomainModel(): UserProfile =
    UserProfile(
        user = User(
            id = UserId(id),
            role = role,
            provider = provider,
            providerId = ProviderId(providerId),
            displayId = DisplayId(displayId),
            name = Name(name),
            profileImageUrl = profileImageUrl,
            introduce = Introduce(introduce),
            lastLoginAt = lastLoginAt,
            active = active,
        ),
        friendsCount = friendsCount,
    )
