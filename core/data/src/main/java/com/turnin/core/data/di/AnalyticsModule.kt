package com.turnin.core.data.di

import com.turnin.core.data.analytics.FirebaseAnalyticsTracker
import com.turnin.core.domain.util.analytics.AnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AnalyticsModule {
    @Binds
    @Singleton
    fun bindsAnalyticsTracker(
        impl: FirebaseAnalyticsTracker,
    ): AnalyticsTracker
}
