package com.peekr.data.shared.di

import com.peekr.data.BuildConfig
import com.peekr.data.account.network.AccountApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
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

    // TODO: timeout 설정
    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): Retrofit.Builder {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
//            .callTimeout(1, TimeUnit.MINUTES)
//            .readTimeout(3, TimeUnit.SECONDS)
//            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit
            .Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BuildConfig.PEEKR_LOCAL_SERVER_URL)
            .client(client)
    }

    @Singleton
    @Provides
    fun provideAccountApi(retrofit: Retrofit.Builder): AccountApi =
        retrofit.build().create(AccountApi::class.java)
}
