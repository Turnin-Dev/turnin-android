package com.peekr.data.userKeyword.di

import com.peekr.data.userKeyword.network.UserKeywordDataSource
import com.peekr.data.userKeyword.network.UserKeywordDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UserKeywordBindModule {
    @Binds
    fun bindsUserKeywordDataSource(impl: UserKeywordDataSourceImpl): UserKeywordDataSource
}
