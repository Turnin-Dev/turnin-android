package com.peekr.domain.keywordDetail.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId

/**
 * 키워드 상세 정보 모델
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
data class KeywordDetail(
    val userKeywordId: UserKeywordId,
    val keywordId: KeywordId,
    val keyword: KeywordName,
    val description: String,
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)
