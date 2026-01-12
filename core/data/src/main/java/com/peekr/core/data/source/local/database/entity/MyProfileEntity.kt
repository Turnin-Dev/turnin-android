package com.peekr.core.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreMyProfile

/**
 * 내 프로필 엔티티
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 * @property lastLoginAt 사용자 마지막 로그인 일자
 * @property friendsCount 친구 수
 * @property active 사용자 활성화 여부
 */
@Entity
data class MyProfileEntity(
    @PrimaryKey
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val lastLoginAt: Long,
    val friendsCount: Long,
    val active: Boolean,
)

fun MyProfileEntity.toDomainModel(): CoreMyProfile =
    CoreMyProfile(
        userId = UserId(userId),
        displayId = DisplayId(displayId),
        name = Name(name),
        profileImageUrl = profileImageUrl,
        introduce = Introduce(introduce),
        lastLoginAt = lastLoginAt,
        friendsCount = friendsCount,
        active = active,
    )

fun CoreMyProfile.toEntity(): MyProfileEntity =
    MyProfileEntity(
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        lastLoginAt = lastLoginAt,
        friendsCount = friendsCount,
        active = active,
    )
