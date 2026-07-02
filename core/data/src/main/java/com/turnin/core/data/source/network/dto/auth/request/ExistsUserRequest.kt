package com.turnin.core.data.source.network.dto.auth.request

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.auth.model.ExistsUser
import com.turnin.core.domain.model.SocialLoginProvider

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

fun ExistsUser.toDataModel(): ExistsUserRequest = ExistsUserRequest(provider, providerId.uid)
