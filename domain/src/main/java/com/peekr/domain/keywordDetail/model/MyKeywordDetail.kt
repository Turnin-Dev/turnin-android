package com.peekr.domain.keywordDetail.model

import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserKeywordId

data class MyKeywordDetail(
    val userKeywordId: UserKeywordId,
    val keywordId: KeywordId,
    val keyword: KeywordValue,
    val description: KeywordDescription,
    val createdAt: Long,
    val updatedAt: Long,
)
