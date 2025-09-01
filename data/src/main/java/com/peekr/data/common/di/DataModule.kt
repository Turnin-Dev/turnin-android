package com.peekr.data.common.di

import com.peekr.data.BuildConfig
import com.peekr.data.account.network.AccountApi
import com.peekr.data.common.retrofit.TokenAuthenticator
import com.peekr.data.common.retrofit.TokenInterceptor
import com.peekr.domain.common.dataStore.DataStoreManager
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
class DataModule {
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

    // ------------------------------ Retrofit ------------------------------
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi,
    ): Retrofit.Builder = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.PEEKR_LOCAL_SERVER_URL)

    @Singleton
    @Provides
    fun provideTokenAuthenticator(
        dataStoreManager: DataStoreManager,
        accountApi: AccountApi,
    ): TokenAuthenticator = TokenAuthenticator(dataStoreManager, accountApi)

    @Singleton
    @Provides
    fun provideTokenInterceptor(dataStoreManager: DataStoreManager): TokenInterceptor =
        TokenInterceptor(dataStoreManager)
}

// ------------------------------ Qualifier ------------------------------
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttpClient

// ------------------------------ Utils ------------------------------
private fun OkHttpClient.Builder.commonTimeout(): OkHttpClient.Builder =
    this
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
