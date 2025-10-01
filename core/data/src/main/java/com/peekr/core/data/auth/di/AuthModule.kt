package com.peekr.core.data.auth.di

import com.peekr.core.data.auth.network.AuthApi
import com.peekr.core.data.network.DefaultOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Singleton
    @Provides
    fun provideAuthApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): AuthApi =
        retrofit.client(okHttpClient).build().create(AuthApi::class.java)
}
