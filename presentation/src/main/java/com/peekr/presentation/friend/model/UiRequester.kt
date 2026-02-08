package com.peekr.presentation.friend.model

import com.peekr.core.domain.friend.model.IncomingRequest

/**
 * UI용 나에게 들어온 친구 요청
 *
 * @property id 친구 ID
 * @property userId 친구의 사용자 ID
 * @property displayId 친구의 사용자 표시 ID
 * @property name 친구의 사용자 이름
 * @property profileImageUrl 친구의 프로필 사진 url
 * @property respondedAt 요청 응답 일자
 * @property createdAt 요청 생성 일자
 * @property updatedAt 요청 수정 일자
 *
 * @see IncomingRequest
 */
data class UiRequester(
    val id: Long,
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val respondedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)

fun IncomingRequest.toUiModel(): UiRequester =
    UiRequester(
        id = id.value,
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        respondedAt = respondedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
