package com.peekr.core.data.source.network.dto.keywordGraph.response

import com.squareup.moshi.JsonClass

/**
 * 사용자 노드 응답 바디
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 사용자 프로필 사진 URL
 */
@JsonClass(generateAdapter = true)
data class UserNodeResponse(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
)
