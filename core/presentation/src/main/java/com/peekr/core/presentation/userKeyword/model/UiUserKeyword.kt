package com.peekr.core.presentation.userKeyword.model

import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.UserKeyword

/**
 * UI용 사용자 키워드
 *
 * @property id 사용자 키워드 ID
 * @property keywordId 키워드 ID
 * @property keywordName 키워드 명
 * @property userId 사용자 ID
 * @property offsetX 키워드 위치 오프셋 X
 * @property offsetY 키워드 위치 오프셋 Y
 * @property createdAt 키워드 생성 일자
 * @property updatedAt 키워드 수정 일자
 */
data class UiUserKeyword(
    val id: UserKeywordId,
    val keywordId: KeywordId,
    val keywordName: String,
    val userId: UserId,
    val offsetX: Double,
    val offsetY: Double,
    val createdAt: Long,
    val updatedAt: Long,
) {
    companion object {
        val samples = List(5) {
            UiUserKeyword(
                id = UserKeywordId((it + 1).toLong()),
                keywordId = KeywordId((it + 1).toLong()),
                keywordName = "Label ${it + 1}",
                userId = UserId(1L),
                offsetY = 0.0,
                offsetX = 0.0,
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
        userId = userId,
        offsetX = offsetX,
        offsetY = offsetY,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun List<UserKeyword>.toUiModel(): List<UiUserKeyword> = this.map { it.toUiModel() }
