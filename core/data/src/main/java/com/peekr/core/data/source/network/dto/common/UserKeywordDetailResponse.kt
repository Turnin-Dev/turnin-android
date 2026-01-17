package com.peekr.core.data.source.network.dto.common

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserInfo
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import com.squareup.moshi.JsonClass

/**
 * 사용자 키워드 상세 정보 응답 바디의 사용자 정보 부분
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 */
@JsonClass(generateAdapter = true)
data class UserInfoResponse(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
)

/**
 * 사용자 키워드 상세 정보 응답 바디
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property userInfo 사용자 정보 [UserInfoResponse]
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
@JsonClass(generateAdapter = true)
data class UserKeywordDetailResponse(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
    val description: String,
    val userInfo: UserInfoResponse,
    val createdAt: Long,
    val updatedAt: Long,
)

fun UserInfoResponse.toDomainModel(): UserInfo =
    UserInfo(
        userId = UserId(userId),
        userName = Name(userName),
        profileImageUrl = profileImageUrl,
    )

fun UserKeywordDetailResponse.toDomainModel(): UserKeywordDetail =
    UserKeywordDetail(
        userKeywordId = UserKeywordId(userKeywordId),
        keywordId = KeywordId(keywordId),
        keywordName = KeywordName(keywordName),
        description = KeywordDescription(description),
        userInfo = userInfo.toDomainModel(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
