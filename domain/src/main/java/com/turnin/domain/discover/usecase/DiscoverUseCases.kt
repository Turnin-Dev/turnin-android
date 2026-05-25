package com.turnin.domain.discover.usecase

import javax.inject.Inject

class DiscoverUseCases @Inject constructor(
    /**
     * 나의 탐색 컨텍스트 조회
     * @see GetMyDiscoverContextUseCase
     */
    val getMyDiscoverContext: GetMyDiscoverContextUseCase,
    /**
     * 사용자 탐색 컨텍스트 조회
     * @see GetDiscoverContextsUseCase
     */
    val getDiscoverContexts: GetDiscoverContextsUseCase,
    /**
     * 나의 키워드 새로고침 트리거
     * @see RefreshMyKeywordsUseCase
     */
    val refreshMyKeywords: RefreshMyKeywordsUseCase,
    /**
     * 특정 사용자 캐시 무효화
     * @see InvalidateCacheUseCase
     */
    val invalidateCache: InvalidateCacheUseCase,
)
