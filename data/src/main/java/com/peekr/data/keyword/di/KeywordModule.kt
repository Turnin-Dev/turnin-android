package com.peekr.data.keyword.di

import com.peekr.data.common.di.DefaultOkHttpClient
import com.peekr.data.keyword.network.KeywordApi
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
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): KeywordApi = retrofit
        .client(okHttpClient)
        .build()
        .create(KeywordApi::class.java)
}
