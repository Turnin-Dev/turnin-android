package com.turnin.core.presentation.ui.model

import com.turnin.core.domain.userKeyword.model.UserKeywordDetail

/**
 * UI용 사용자 키워드 상세 정보 모델
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UiUserKeywordDetail(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
    val description: String,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

fun UserKeywordDetail.toUiModel(): UiUserKeywordDetail =
    UiUserKeywordDetail(
        userKeywordId = userKeywordId.value,
        keywordId = keywordId.value,
        keywordName = keywordName.value,
        description = description.value,
        userId = userInfo.userId.value,
        userName = userInfo.userName.value,
        profileImageUrl = userInfo.profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
