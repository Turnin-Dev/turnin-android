package com.peekr.core.data.source.local.di

import android.content.Context
import androidx.room.Room
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.local.database.dao.FeedRemoteKeyDao
import com.peekr.core.data.source.local.database.dao.MyKeywordDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.dao.UserKeywordDetailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PeekrDatabase = Room
        .databaseBuilder(context, PeekrDatabase::class.java, "peekr.db")
        .build()

    @Provides
    @Singleton
    fun provideMyProfileDao(database: PeekrDatabase): MyProfileDao =
        database.myProfileDao()

    @Provides
    @Singleton
    fun provideMyKeywordDao(database: PeekrDatabase): MyKeywordDao =
        database.myKeywordDao()

    @Provides
    @Singleton
    fun provideUserKeywordDetailDao(database: PeekrDatabase): UserKeywordDetailDao =
        database.userKeywordDetailDao()

    @Provides
    @Singleton
    fun provideFeedRemoteKeyDao(database: PeekrDatabase): FeedRemoteKeyDao =
        database.feedRemoteKeyDao()
}
