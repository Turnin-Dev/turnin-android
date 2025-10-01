package com.peekr.core.domain.userKeyword.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId

/**
 * 사용자 키워드 생성 요청
 *
 * @property userId 사용자 ID
 * @property keywordId 키워드 ID
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property description 키워드 설명
 */
data class CreateUserKeyword(
    val userId: UserId,
    val keywordId: KeywordId,
    val offsetX: Double,
    val offsetY: Double,
    val description: String,
)
