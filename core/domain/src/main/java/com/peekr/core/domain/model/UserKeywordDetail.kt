package com.peekr.core.domain.model

/**
 * 사용자 키워드 상세 정보 모델의 사용자 정보 부분
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 */
data class UserInfo(
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
)

/**
 * 사용자 키워드 상세 정보 모델
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keyword 키워드
 * @property description 키워드 내용
 * @property userInfo 사용자 정보 [UserInfo]
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UserKeywordDetail(
    val userKeywordId: UserKeywordId,
    val keywordId: KeywordId,
    val keyword: KeywordName,
    val description: String,
    val userInfo: UserInfo?,
    val createdAt: Long,
    val updatedAt: Long,
)
