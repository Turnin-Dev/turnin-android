package com.turnin.core.common.di

import android.content.Context
import com.turnin.core.common.util.AppVersionProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VersionModule {
    @Provides
    @Singleton
    fun provideAppVersionProvider(
        @ApplicationContext context: Context,
    ): AppVersionProvider =
        AppVersionProvider(context)
}
