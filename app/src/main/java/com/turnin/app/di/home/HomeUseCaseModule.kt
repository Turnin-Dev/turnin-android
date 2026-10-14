package com.turnin.app.di.home

import com.turnin.core.domain.feed.repository.FeedRepository
import com.turnin.domain.home.usecase.GetFeedsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 홈 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object HomeUseCaseModule {
    @Provides
    fun provideGetFeedsUseCase(
        feedRepository: FeedRepository,
    ): GetFeedsUseCase =
        GetFeedsUseCase(
            feedRepository = feedRepository,
        )
}
