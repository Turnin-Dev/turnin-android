package com.peekr.core.data.userKeyword.di

import com.peekr.core.data.userKeyword.network.UserKeywordDataSource
import com.peekr.core.data.userKeyword.network.UserKeywordDataSourceImpl
import com.peekr.core.data.userKeyword.repository.UserKeywordRepositoryImpl
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UserKeywordBindModule {
    @Binds
    fun bindsUserKeywordDataSource(impl: UserKeywordDataSourceImpl): UserKeywordDataSource

    @Binds
    fun bindsUserKeywordRepository(impl: UserKeywordRepositoryImpl): UserKeywordRepository
}
