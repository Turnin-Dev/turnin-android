package com.turnin.presentation.home.model

import com.turnin.core.common.util.toRelativeTime
import com.turnin.core.domain.feed.model.Feed

/**
 * UI용 피드
 *
 * @property userKeywordId 사용자 키워드 ID (피드를 구분하는 주 키)
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 * @property keywordId 키워드 ID
 * @property keyword 키워드 명
 * @property description 키워드 내용
 * @property createdAt 키워드 생성 일자
 * @property score 피드 점수(피드 표시 조건을 위한 점수, 높을수록 피드가 표시될 확률이 높음)
 * @property similarity 유사도(사용자의 키워드들과 유사한 정도를 나타냄, 1.0에 가까울수록 유사함)
 */
data class UiFeed(
    val userKeywordId: Long,
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
    val keywordId: Long,
    val keyword: String,
    val description: String,
    val createdAt: String,
    val score: Double,
    val similarity: Double,
) {
    companion object {
        val sample = UiFeed(
            userKeywordId = 1L,
            userId = 1L,
            userName = "username",
            profileImageUrl = null,
            keywordId = 1L,
            keyword = "Keyword",
            description = "대통령은 제4항과 제5항의 규정에 의하여 확정된 법률을 지체없이 공포하여야 한다." +
                "제5항에 의하여 법률이 확정된 후 또는 제4항에 의한 확정법률이 정부에 이송된 후" +
                "5일 이내에 대통령이 공포하지 아니할 때에는 국회의장이 이를 공포한다.",
            createdAt = "2026.01.01",
            score = 50.0,
            similarity = 0.8,
        )
    }
}

fun Feed.toUiModel(): UiFeed =
    UiFeed(
        userKeywordId = userKeywordId.value,
        userId = userId.value,
        userName = userName.value,
        profileImageUrl = profileImageUrl,
        keywordId = keywordId.value,
        keyword = keyword.value,
        description = description.value,
        createdAt = createdAt.toRelativeTime(false),
        score = score,
        similarity = similarity,
    )
