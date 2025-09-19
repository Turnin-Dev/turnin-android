package com.peekr.data.userKeyword.di

import com.peekr.data.userKeyword.network.UserKeywordDataSource
import com.peekr.data.userKeyword.network.UserKeywordDataSourceImpl
import com.peekr.data.userKeyword.repository.UserKeywordRepositoryImpl
import com.peekr.domain.userKeyword.repository.UserKeywordRepository
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
