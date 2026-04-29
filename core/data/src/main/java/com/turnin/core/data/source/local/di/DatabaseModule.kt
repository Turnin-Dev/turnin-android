package com.turnin.core.data.source.local.di

import android.content.Context
import androidx.room.Room
import com.turnin.core.data.cleaner.Clearable
import com.turnin.core.data.source.local.database.PeekrDatabase
import com.turnin.core.data.source.local.database.dao.FeedDao
import com.turnin.core.data.source.local.database.dao.FeedRemoteKeyDao
import com.turnin.core.data.source.local.database.dao.MyKeywordDao
import com.turnin.core.data.source.local.database.dao.MyProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Multi Binding
    @Provides
    @IntoSet
    fun provideDatabaseClearable(
        database: PeekrDatabase,
    ): Clearable = Clearable {
        database.clearAllTables()
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): PeekrDatabase = Room
        .databaseBuilder(context, PeekrDatabase::class.java, "peekr_v1.db")
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
    fun provideFeedDao(database: PeekrDatabase): FeedDao =
        database.feedDao()

    @Provides
    @Singleton
    fun provideFeedRemoteKeyDao(database: PeekrDatabase): FeedRemoteKeyDao =
        database.feedRemoteKeyDao()
}
