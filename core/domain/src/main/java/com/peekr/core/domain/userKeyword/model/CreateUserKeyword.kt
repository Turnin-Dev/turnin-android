package com.peekr.core.domain.userKeyword.model

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserId

/**
 * 사용자 키워드 생성 요청
 *
 * @property userId 사용자 ID
 * @property keyword 키워드 명
 * @property description 키워드 설명
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 */
data class CreateUserKeyword(
    val userId: UserId,
    val keyword: KeywordValue,
    val description: KeywordDescription,
    val offsetX: Double,
    val offsetY: Double,
)
