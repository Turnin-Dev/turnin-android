package com.peekr.core.data.source.local.di

import android.content.Context
import androidx.room.Room
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        moshi: Moshi,
    ): PeekrDatabase = Room.databaseBuilder(context, PeekrDatabase::class.java, "peekr.db")
        .build()

    @Provides
    @Singleton
    fun provideMyProfileDao(database: PeekrDatabase): MyProfileDao =
        database.myProfileDao()
}
