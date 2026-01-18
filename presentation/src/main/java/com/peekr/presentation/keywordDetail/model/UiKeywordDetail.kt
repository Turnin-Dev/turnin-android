package com.peekr.presentation.keywordDetail.model

import com.peekr.domain.keywordDetail.model.KeywordDetail

/**
 * UI용 키워드 상세 정보 모델
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keyword 키워드
 * @property description 키워드 내용
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UiKeywordDetail(
    val userKeywordId: Long,
    val keywordId: Long,
    val keyword: String,
    val description: String,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

fun KeywordDetail.toUiModel(): UiKeywordDetail =
    UiKeywordDetail(
        userKeywordId = userKeywordId.value,
        keywordId = keywordId.value,
        keyword = keyword.value,
        description = description.value,
        userId = userId.value,
        userName = userName.value,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
