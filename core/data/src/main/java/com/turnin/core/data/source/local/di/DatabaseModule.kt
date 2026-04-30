package com.turnin.core.data.source.local.di

import android.content.Context
import androidx.room.Room
import com.turnin.core.data.cleaner.Clearable
import com.turnin.core.data.source.local.database.TurninDatabase
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
        database: TurninDatabase,
    ): Clearable = Clearable {
        database.clearAllTables()
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TurninDatabase = Room
        .databaseBuilder(context, TurninDatabase::class.java, TurninDatabase.DATABASE_NAME)
        .build()

    @Provides
    @Singleton
    fun provideMyProfileDao(database: TurninDatabase): MyProfileDao =
        database.myProfileDao()

    @Provides
    @Singleton
    fun provideMyKeywordDao(database: TurninDatabase): MyKeywordDao =
        database.myKeywordDao()

    @Provides
    @Singleton
    fun provideFeedDao(database: TurninDatabase): FeedDao =
        database.feedDao()

    @Provides
    @Singleton
    fun provideFeedRemoteKeyDao(database: TurninDatabase): FeedRemoteKeyDao =
        database.feedRemoteKeyDao()
}
