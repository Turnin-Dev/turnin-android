package com.peekr.data.login.di

import com.peekr.data.login.util.SocialAuthManagerFactoryImpl
import com.peekr.domain.login.util.SocialAuthManagerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LoginBindModule {
    @Binds
    fun bindsAuthManagerFactory(impl: SocialAuthManagerFactoryImpl): SocialAuthManagerFactory
}
