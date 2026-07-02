package com.turnin.core.domain.file.model

/**
 * 사전 정의된 URL
 *
 * @property presignedUrl 사전 정의 URL
 * @property method 사전 정의 URL로 요청할 때 필요한 메서드
 * @property expiresInSeconds 사전 정의 URL 만료 시간 (초 단위)
 */
data class PresignedUrl(
    val presignedUrl: String,
    val method: String,
    val expiresInSeconds: Int,
)
