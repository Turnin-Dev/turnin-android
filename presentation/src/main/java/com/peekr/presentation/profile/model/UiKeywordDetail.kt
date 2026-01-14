package com.peekr.presentation.profile.model

import com.peekr.core.domain.model.UserKeywordDetail

/**
 * UI용 나의 키워드 상세 정보
 *
 * @property userKeywordId 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UiKeywordDetail(
    val userKeywordId: Long,
    val keywordId: Long,
    val keywordName: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    companion object {
        val samples = List(5) {
            UiKeywordDetail(
                userKeywordId = (it + 1).toLong(),
                keywordId = (it + 1).toLong(),
                keywordName = "Label ${it + 1}",
                description = "Description ${it + 1}",
                createdAt = 0L,
                updatedAt = 0L,
            )
        }
    }
}

fun UserKeywordDetail.toUiModel(): UiKeywordDetail =
    UiKeywordDetail(
        userKeywordId = userKeywordId.value,
        keywordId = keywordId.value,
        keywordName = this@toUiModel.keywordName.value,
        description = description.value,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
