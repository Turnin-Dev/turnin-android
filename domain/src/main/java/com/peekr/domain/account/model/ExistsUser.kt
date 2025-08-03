package com.peekr.domain.account.model

data class ExistsUser(
    val provider: SocialLoginProvider,
    val providerId: String,
)
