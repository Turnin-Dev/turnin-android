package com.peekr.domain.profile.model

/**
 * 프로필 비즈니스 규칙
 */
object ProfileRule {
    /**
     * 최대로 등록할 수 있는 키워드 개수
     */
    const val MAX_KEYWORD_COUNT = 5

    /**
     * 사용자가 키워드 등록 개수 제한을 초과했는지 검사
     */
    fun isKeywordLimitExceed(currentCount: Int): Boolean =
        currentCount >= MAX_KEYWORD_COUNT
}
