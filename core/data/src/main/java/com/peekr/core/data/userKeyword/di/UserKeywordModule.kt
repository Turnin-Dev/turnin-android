package com.peekr.core.data.userKeyword.di

import com.peekr.core.data.network.TokenOkHttpClient
import com.peekr.core.data.userKeyword.network.UserKeywordApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UserKeywordModule {
    @Singleton
    @Provides
    fun providerUserKeywordApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): UserKeywordApi = retrofit
        .client(okHttpClient)
        .build()
        .create(UserKeywordApi::class.java)
}
