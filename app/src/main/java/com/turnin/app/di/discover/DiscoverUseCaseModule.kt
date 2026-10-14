package com.turnin.app.di.discover

import com.turnin.core.domain.discover.repository.DiscoverRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.discover.usecase.DiscoverUseCases
import com.turnin.domain.discover.usecase.GetDiscoverContextsUseCase
import com.turnin.domain.discover.usecase.GetMyDiscoverContextUseCase
import com.turnin.domain.discover.usecase.InvalidateCacheUseCase
import com.turnin.domain.discover.usecase.RefreshMyKeywordsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 탐색 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object DiscoverUseCaseModule {
    @Provides
    fun provideDiscoverUseCases(
        getMyDiscoverContext: GetMyDiscoverContextUseCase,
        getDiscoverContexts: GetDiscoverContextsUseCase,
        refreshMyKeywords: RefreshMyKeywordsUseCase,
        invalidateCache: InvalidateCacheUseCase,
    ): DiscoverUseCases =
        DiscoverUseCases(
            getMyDiscoverContext = getMyDiscoverContext,
            getDiscoverContexts = getDiscoverContexts,
            refreshMyKeywords = refreshMyKeywords,
            invalidateCache = invalidateCache,
        )

    @Provides
    fun provideGetDiscoverContextsUseCase(
        discoverRepository: DiscoverRepository,
    ): GetDiscoverContextsUseCase =
        GetDiscoverContextsUseCase(
            discoverRepository = discoverRepository,
        )

    @Provides
    fun provideGetMyDiscoverContextUseCase(
        userRepository: UserRepository,
        userKeywordRepository: UserKeywordRepository,
    ): GetMyDiscoverContextUseCase =
        GetMyDiscoverContextUseCase(
            userRepository = userRepository,
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideInvalidateCacheUseCase(
        discoverRepository: DiscoverRepository,
    ): InvalidateCacheUseCase =
        InvalidateCacheUseCase(
            discoverRepository = discoverRepository,
        )

    @Provides
    fun provideRefreshMyKeywordsUseCase(
        repository: UserKeywordRepository,
    ): RefreshMyKeywordsUseCase =
        RefreshMyKeywordsUseCase(
            repository = repository,
        )
}
