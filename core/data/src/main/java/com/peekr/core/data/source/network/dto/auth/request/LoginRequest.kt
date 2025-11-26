package com.peekr.core.data.source.network.dto.auth.request

import com.peekr.core.domain.auth.model.LoginCredentials
import com.peekr.core.domain.model.SocialLoginProvider
import com.squareup.moshi.JsonClass

/**
 * 로그인 요청 바디
 *
 * @property provider 소셜로그인 플랫폼
 * @property providerId 소셜로그인 플랫폼에서 제공된 id
 */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    val provider: SocialLoginProvider,
    val providerId: String,
)

fun LoginCredentials.toDataModel(): LoginRequest = LoginRequest(provider, providerId.uid)
