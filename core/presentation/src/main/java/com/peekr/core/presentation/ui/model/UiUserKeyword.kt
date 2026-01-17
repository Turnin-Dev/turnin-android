package com.peekr.core.presentation.ui.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword

/**
 * UI용 사용자 키워드
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UiUserKeyword(
    val id: UserKeywordId,
    val keywordId: KeywordId,
    val keywordName: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    companion object {
        val samples = List(5) {
            UiUserKeyword(
                id = UserKeywordId((it + 1).toLong()),
                keywordId = KeywordId((it + 1).toLong()),
                keywordName = "Label ${it + 1}",
                description = "Description ${it + 1}",
                createdAt = 0L,
                updatedAt = 0L,
            )
        }
    }
}

fun UserKeyword.toUiModel(): UiUserKeyword =
    UiUserKeyword(
        id = id,
        keywordId = keywordId,
        keywordName = keyword.value,
        description = description.value,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun List<UserKeyword>.toUiModel(): List<UiUserKeyword> = this.map { it.toUiModel() }
