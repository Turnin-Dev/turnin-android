package com.peekr.data.account.di

import com.peekr.data.account.util.AuthManagerFactoryImpl
import com.peekr.domain.account.util.AuthManagerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AccountBindModule {
    @Binds
    fun bindsAuthManagerFactory(impl: AuthManagerFactoryImpl): AuthManagerFactory
}
