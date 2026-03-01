package com.peekr.core.data.di

import android.content.Context
import com.peekr.core.data.source.network.social.GoogleSocialAuthManager
import com.peekr.core.data.source.network.social.KakaoSocialAuthManager
import com.peekr.core.data.source.network.social.SocialAuthManagerFactoryImpl
import com.peekr.core.domain.auth.social.SocialAuthManager
import com.peekr.core.domain.auth.social.SocialAuthManagerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SocialAuthModule {
    @GoogleAuth
    @Singleton
    @Provides
    fun provideGoogleAuthManager(
        @ApplicationContext context: Context,
    ): SocialAuthManager = GoogleSocialAuthManager(context)

    @KakaoAuth
    @Singleton
    @Provides
    fun provideKakaoAuthManager(
        @ApplicationContext context: Context,
    ): SocialAuthManager = KakaoSocialAuthManager(context)
}

@Module
@InstallIn(SingletonComponent::class)
interface LoginBindModule {
    @Binds
    fun bindsAuthManagerFactory(impl: SocialAuthManagerFactoryImpl): SocialAuthManagerFactory
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoAuth
