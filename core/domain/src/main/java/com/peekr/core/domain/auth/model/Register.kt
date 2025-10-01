package com.peekr.core.domain.auth.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.ProviderId
import com.peekr.core.domain.model.SocialLoginProvider

/**
 * 회원가입 시 사용 한다.
 *
 * @param provider 소셜로그인 플랫폼 ([com.peekr.domain.account.model.SocialLoginProvider])
 * @param providerId 소셜로그인 플랫폼에서 제공하는 고유 ID ([com.peekr.domain.account.model.ProviderId])
 * @param displayId 사용자 표시 ID ([DisplayId])
 * @param name 사용자 이름
 * @param profileImageUrl 사용자 프로필 이미지 url
 * @param introduce 사용자 소개 글
 */
data class Register(
    val provider: SocialLoginProvider,
    val providerId: ProviderId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce?,
)
