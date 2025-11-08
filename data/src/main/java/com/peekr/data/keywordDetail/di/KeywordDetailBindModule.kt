package com.peekr.data.keywordDetail.di

import com.peekr.data.keywordDetail.repository.KeywordDetailRepositoryImpl
import com.peekr.domain.keywordDetail.repository.KeywordDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface KeywordDetailBindModule {
    @Binds
    fun bindsKeywordDetailRepository(impl: KeywordDetailRepositoryImpl): KeywordDetailRepository
}
