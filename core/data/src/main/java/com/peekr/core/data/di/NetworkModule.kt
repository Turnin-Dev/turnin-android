package com.peekr.core.data.di

import com.peekr.core.data.BuildConfig
import com.peekr.core.data.eventBus.AuthEventBus
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.api.RefreshTokenApi
import com.peekr.core.data.source.network.retrofit.TokenAuthenticator
import com.peekr.core.data.source.network.retrofit.TokenInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    // ------------------------------ Serialization ------------------------------
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                setLevel(HttpLoggingInterceptor.Level.NONE)
            }
            redactHeader("Authorization")
            redactHeader("Cookie")
        }

    // ------------------------------ OkHttpClient & Interceptor ------------------------------
    @DefaultOkHttpClient
    @Singleton
    @Provides
    fun provideDefaultOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .commonTimeout()
        .build()

    @TokenOkHttpClient
    @Singleton
    @Provides
    fun providerTokenOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .authenticator(tokenAuthenticator)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(tokenInterceptor)
        .commonTimeout()
        .build()

    // ------------------------------ Retrofit ------------------------------
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi,
    ): Retrofit.Builder = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.PEEKR_MOCK_SERVER_URL)

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        dataStoreManager: DataStoreManager,
        refreshTokenApi: RefreshTokenApi,
        authEventBus: AuthEventBus,
    ): TokenAuthenticator =
        TokenAuthenticator(dataStoreManager, refreshTokenApi, authEventBus)

    @Singleton
    @Provides
    fun provideTokenInterceptor(dataStoreManager: DataStoreManager): TokenInterceptor =
        TokenInterceptor(dataStoreManager)

    @Singleton
    @Provides
    fun providerRefreshTokenApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): RefreshTokenApi = retrofit
        .client(okHttpClient)
        .build()
        .create(RefreshTokenApi::class.java)
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenOkHttpClient

// ------------------------------ Utils ------------------------------
private fun OkHttpClient.Builder.commonTimeout(): OkHttpClient.Builder =
    this
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
