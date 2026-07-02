package com.turnin.core.data.di

import android.content.Context
import com.turnin.core.data.source.network.social.GoogleSocialAuthManager
import com.turnin.core.data.source.network.social.KakaoSocialAuthManager
import com.turnin.core.data.source.network.social.SocialAuthManagerFactoryImpl
import com.turnin.core.domain.auth.social.SocialAuthManager
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
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
    fun provideKakaoAuthManager(): SocialAuthManager = KakaoSocialAuthManager()
}

@Module
@InstallIn(SingletonComponent::class)
interface AuthManagerFactoryBindModule {
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
