package com.turnin.core.domain.discover.model

import com.turnin.core.domain.model.UserId

/**
 * 탐색 캐시 키
 *
 * @property userId 조회 대상 사용자 ID
 * @property cursor 커서 값
 */
data class DiscoverCacheKey(
    val userId: UserId,
    val cursor: Long?,
)
