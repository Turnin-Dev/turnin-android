package com.peekr.core.data.keyword.di

import com.peekr.core.data.keyword.network.KeywordDataSource
import com.peekr.core.data.keyword.network.KeywordNetworkDataSource
import com.peekr.core.data.keyword.repository.KeywordRepositoryImpl
import com.peekr.core.domain.keyword.repository.KeywordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface KeywordBindModule {
    @Binds
    fun bindsKeywordDataSource(impl: KeywordNetworkDataSource): KeywordDataSource

    @Binds
    fun bindsKeywordRepository(impl: KeywordRepositoryImpl): KeywordRepository
}
