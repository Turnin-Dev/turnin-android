package com.peekr.data.account.model.request

import com.peekr.domain.account.model.ExistsUser
import com.peekr.domain.account.model.SocialLoginProvider
import com.squareup.moshi.JsonClass

/**
 * 사용자 존재 여부 확인 요청 바디
 *
 * @property provider 소셜로그인 플랫폼
 * @property providerId 소셜로그인 플랫폼에서 제공된 id
 */
@JsonClass(generateAdapter = true)
data class ExistsUserRequest(
    val provider: SocialLoginProvider,
    val providerId: String,
)

fun ExistsUser.toDataModel(): ExistsUserRequest = ExistsUserRequest(provider, providerId)
