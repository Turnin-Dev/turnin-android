package com.peekr.data.keyword.di

import com.peekr.data.keyword.network.KeywordDataSource
import com.peekr.data.keyword.network.KeywordDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface KeywordBindModule {
    @Binds
    fun bindsKeywordDataSource(impl: KeywordDataSourceImpl): KeywordDataSource
}
