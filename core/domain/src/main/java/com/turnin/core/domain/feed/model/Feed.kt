package com.turnin.core.domain.feed.model

import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId

/**
 * 피드 모델 클래스
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 */
data class Feed(
    val userKeywordId: UserKeywordId,
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
    val keywordId: KeywordId,
    val keyword: KeywordName,
    val description: KeywordDescription,
    val createdAt: Long,
)
