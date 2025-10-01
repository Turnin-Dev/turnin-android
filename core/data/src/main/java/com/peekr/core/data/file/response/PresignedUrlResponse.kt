package com.peekr.core.data.file.response

import com.peekr.core.domain.file.model.PresignedUrl
import com.squareup.moshi.JsonClass

/**
 * 파일 업로드에 필요한 사전 정의 URL 응답 바디
 *
 * @property presignedUrl 사전 정의 URL
 * @property method 사전 정의 URL로 요청할 때 필요한 메서드
 * @property expiresInSeconds 사전 정의 URL 만료 시간 (초 단위)
 */
@JsonClass(generateAdapter = true)
data class PresignedUrlResponse(
    val presignedUrl: String,
    val method: String,
    val expiresInSeconds: Int,
)

fun PresignedUrlResponse.toDomainModel(): PresignedUrl =
    PresignedUrl(
        presignedUrl = presignedUrl,
        method = method,
        expiresInSeconds = expiresInSeconds,
    )
