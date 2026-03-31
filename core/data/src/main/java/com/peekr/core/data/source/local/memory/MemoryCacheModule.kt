package com.peekr.core.data.source.local.memory

import com.peekr.core.data.cleaner.Clearable
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@Module
@InstallIn(SingletonComponent::class)
object MemoryCacheModule {
    // ------------------------------ Multi Binding ------------------------------
    @Provides
    @IntoSet
    fun provideCoreUserProfileCacheClearable(
        cache: MemoryCache<UserId, CoreUserProfile>,
    ): Clearable = Clearable { cache.clear() }

    @Provides
    @IntoSet
    fun provideUserKeywordDetailListCacheClearable(
        cache: MemoryCache<UserId, List<UserKeywordDetail>>,
    ): Clearable = Clearable { cache.clear() }

    @Provides
    @IntoSet
    fun provideUserKeywordDetailCacheClearable(
        cache: MemoryCache<UserKeywordId, UserKeywordDetail>,
    ): Clearable = Clearable { cache.clear() }

    // ------------------------------ Provide Module ------------------------------
    @Provides
    @Singleton
    fun provideCoreUserProfileMemoryCache(): MemoryCache<UserId, CoreUserProfile> =
        LruMemoryCache(
            maxSize = 10,
            ttl = 3.minutes,
            name = "CoreUserProfile",
        )

    @Provides
    @Singleton
    fun provideUserKeywordDetailListMemoryCache(): MemoryCache<UserId, List<UserKeywordDetail>> =
        LruMemoryCache(
            maxSize = 10,
            ttl = 5.minutes,
            name = "UserKeywordDetailList",
        )

    @Provides
    @Singleton
    fun provideUserKeywordDetailMemoryCache(): MemoryCache<UserKeywordId, UserKeywordDetail> =
        LruMemoryCache(
            maxSize = 50,
            ttl = 5.minutes,
            name = "UserKeywordDetail",
        )
}
