package com.peekr.core.data.source.local.memory

import com.peekr.core.data.BuildConfig
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object MemoryCacheModule {
    @Provides
    @Singleton
    fun provideCoreUserProfileMemoryCache(): MemoryCache<Long, CoreUserProfile> =
        LruMemoryCache(
            maxSize = 10,
            ttl = if (!BuildConfig.DEBUG) {
                3.minutes
            } else {
                0.seconds
            },
            name = "CoreUserProfile",
        )

    @Provides
    @Singleton
    fun provideUserKeywordDetailListMemoryCache(): MemoryCache<UserId, List<UserKeywordDetail>> =
        LruMemoryCache(
            maxSize = 10,
            ttl = if (!BuildConfig.DEBUG) {
                5.minutes
            } else {
                0.seconds
            },
            name = "UserKeywordDetailList",
        )

    @Provides
    @Singleton
    fun provideUserKeywordDetailMemoryCache(): MemoryCache<UserKeywordId, UserKeywordDetail> =
        LruMemoryCache(
            maxSize = 50,
            ttl = if (!BuildConfig.DEBUG) {
                5.minutes
            } else {
                0.seconds
            },
            name = "UserKeywordDetail",
        )
}
