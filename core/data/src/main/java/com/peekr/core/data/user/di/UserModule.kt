package com.peekr.core.data.user.di

import com.peekr.core.data.network.TokenOkHttpClient
import com.peekr.core.data.user.network.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UserModule {
    @Provides
    @Singleton
    fun providerUserApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): UserApi =
        retrofit.client(okHttpClient).build().create(UserApi::class.java)
}
