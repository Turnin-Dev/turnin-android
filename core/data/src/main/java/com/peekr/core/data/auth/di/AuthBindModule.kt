package com.peekr.core.data.auth.di

import com.peekr.core.data.auth.network.AuthDataSource
import com.peekr.core.data.auth.network.AuthNetworkDataSource
import com.peekr.core.data.auth.repository.AuthRepositoryImpl
import com.peekr.core.domain.auth.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AuthBindModule {
    @Binds
    fun bindsAuthDataSource(impl: AuthNetworkDataSource): AuthDataSource

    @Binds
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
