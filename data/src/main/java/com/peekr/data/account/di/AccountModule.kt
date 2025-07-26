package com.peekr.data.account.di

import android.content.Context
import com.peekr.data.account.network.AccountApi
import com.peekr.data.account.util.GoogleAuthManager
import com.peekr.data.account.util.KakaoAuthManager
import com.peekr.data.shared.di.DefaultOkHttpClient
import com.peekr.domain.account.util.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AccountModule {
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

    @Singleton
    @Provides
    fun provideAccountApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): AccountApi =
        retrofit.client(okHttpClient).build().create(AccountApi::class.java)
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleAuth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoAuth
