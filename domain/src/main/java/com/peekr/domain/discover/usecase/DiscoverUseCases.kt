package com.peekr.domain.discover.usecase

import javax.inject.Inject

class DiscoverUseCases @Inject constructor(
    /** @see GetMyDiscoverContextUseCase */
    val getMyDiscoverContext: GetMyDiscoverContextUseCase,
    /** @see GetDiscoverContextsUseCase */
    val getDiscoverContexts: GetDiscoverContextsUseCase,
)
