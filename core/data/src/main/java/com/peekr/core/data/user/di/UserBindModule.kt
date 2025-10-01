package com.peekr.core.data.user.di

import com.peekr.core.data.user.network.UserDataSource
import com.peekr.core.data.user.network.UserNetworkDataSource
import com.peekr.core.data.user.repository.UserRepositoryImpl
import com.peekr.core.domain.user.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UserBindModule {
    @Binds
    fun bindsUserDataSource(impl: UserNetworkDataSource): UserDataSource

    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}
