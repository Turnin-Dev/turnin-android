package com.peekr.data.shared.di

import com.peekr.data.BuildConfig
import com.peekr.data.account.network.AccountApi
import com.peekr.data.shared.retrofit.TokenAuthenticator
import com.peekr.data.shared.retrofit.TokenInterceptor
import com.peekr.domain.shared.dataStore.DataStoreManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
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
    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .build()

    // ------------------------------ Retrofit ------------------------------
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi,
        okHttpClient: OkHttpClient,
    ): Retrofit.Builder = Retrofit
        .Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BuildConfig.PEEKR_LOCAL_SERVER_URL)
        .client(okHttpClient)

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
