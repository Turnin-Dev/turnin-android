package com.turnin.core.domain.userKeyword.model

import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.UserId

/**
 * 사용자 키워드 생성 요청
 *
 * @property userId 사용자 ID
 * @property keyword 키워드 명
 * @property description 키워드 설명
 */
data class CreateUserKeyword(
    val userId: UserId,
    val keyword: KeywordName,
    val description: KeywordDescription,
)
