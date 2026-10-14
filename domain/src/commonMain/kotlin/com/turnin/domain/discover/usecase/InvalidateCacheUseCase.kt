package com.turnin.domain.discover.usecase

import com.turnin.core.domain.discover.repository.DiscoverRepository
import com.turnin.core.domain.model.UserId

/**
 * 탐색 캐시 무효화
 */
class InvalidateCacheUseCase(
    private val discoverRepository: DiscoverRepository,
) {
    /**
     * 특정 사용자의 탐색 캐시 무효화
     *
     * @param userId 대상 사용자 ID
     */
    operator fun invoke(userId: Long) =
        discoverRepository.invalidateCache(UserId(userId))
}
