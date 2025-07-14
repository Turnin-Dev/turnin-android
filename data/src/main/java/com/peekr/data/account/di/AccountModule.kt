package com.peekr.data.account.di

import android.content.Context
import com.peekr.data.account.util.AuthManagerFactoryImpl
import com.peekr.data.account.util.GoogleAuthManager
import com.peekr.data.account.util.KakaoAuthManager
import com.peekr.domain.account.util.AuthManager
import com.peekr.domain.account.util.AuthManagerFactory
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
object AccountModule {
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

@Module
@InstallIn(SingletonComponent::class)
interface AccountBindModule {
    @Binds
    fun bindsAuthManagerFactory(impl: AuthManagerFactoryImpl): AuthManagerFactory
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoAuth
