package com.peekr.data.keyword.model.request

/**
 * 키워드 생성 요청 바디
 *
 * @property keyword 키워드명
 */
data class CreateKeywordRequest(
    val keyword: String,
)
