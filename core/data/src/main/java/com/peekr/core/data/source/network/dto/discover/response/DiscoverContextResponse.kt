package com.peekr.core.data.source.network.dto.discover.response

import com.peekr.core.domain.discover.model.DiscoverContext
import com.squareup.moshi.JsonClass

/**
 * 탐색 컨텍스트 응답 바디
 *
 * 탐색에 필요한 정보를 담고 있다.
 *
 * - 담고 있는 정보: 사용자 정보 일부 + 키워드 정보 일부
 *
 * @property user 탐색용 사용자
 * @property keywords 탐색용 키워드 리스트
 */
@JsonClass(generateAdapter = true)
data class DiscoverContextResponse(
    val user: DiscoverUserResponse,
    val keywords: List<DiscoverKeywordResponse>,
)

fun DiscoverContextResponse.toDomainModel(): DiscoverContext =
    DiscoverContext(
        user = user.toDomainModel(),
        keywords = keywords.map { it.toDomainModel() },
    )
