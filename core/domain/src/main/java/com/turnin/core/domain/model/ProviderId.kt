package com.turnin.core.domain.model

/**
 * 외부 서비스에서 제공된 사용자의 고유 ID를 래핑한다.
 *
 * 보통, 소셜로그인 제공자가 제공한 ID를 래핑한다.
 *
 * @property uid Firebase 등에서 발급된 고유 식별자
 */
data class ProviderId(val uid: String)
