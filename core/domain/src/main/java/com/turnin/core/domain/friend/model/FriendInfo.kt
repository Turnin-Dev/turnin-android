package com.turnin.core.domain.friend.model

import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId

/**
 * 친구 정보
 *
 * @property id 친구 ID
 * @property userId 친구의 사용자 ID
 * @property displayId 친구의 사용자 표시 ID
 * @property name 친구의 사용자 이름
 * @property profileImageUrl 친구의 프로필 사진 url
 * @property respondedAt 요청 응답 일자
 * @property createdAt 요청 생성 일자
 * @property updatedAt 요청 수정 일자
 */
data class FriendInfo(
    val id: FriendId,
    val userId: UserId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val respondedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)
