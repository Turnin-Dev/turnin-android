package com.peekr.core.data.keyword.di

import com.peekr.core.data.keyword.network.KeywordApi
import com.peekr.core.data.network.TokenOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class KeywordModule {
    @Singleton
    @Provides
    fun providerKeywordApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): KeywordApi = retrofit
        .client(okHttpClient)
        .build()
        .create(KeywordApi::class.java)
}
