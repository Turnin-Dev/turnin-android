package com.peekr.data.login.di

import android.content.Context
import com.peekr.data.login.util.GoogleAuthManager
import com.peekr.data.login.util.KakaoAuthManager
import com.peekr.domain.login.util.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LoginModule {
    @GoogleAuth
    @Singleton
    @Provides
    fun provideGoogleAuthManager(
        @ApplicationContext context: Context,
    ): AuthManager = GoogleAuthManager(context)

    @KakaoAuth
    @Singleton
    @Provides
    fun provideKakaoAuthManager(
        @ApplicationContext context: Context,
    ): AuthManager = KakaoAuthManager(context)
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoAuth
