package com.turnin.core.domain.auth.model

import com.turnin.core.domain.model.ProviderId
import com.turnin.core.domain.model.SocialLoginProvider

/**
 * 이미 존재하는 사용자를 파악하기 위한 사용자 식별 정보
 *
 * @property provider 소셜 로그인 플랫폼
 * @property providerId 플랫폼별 사용자 고유 ID
 */
data class ExistsUser(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
)
