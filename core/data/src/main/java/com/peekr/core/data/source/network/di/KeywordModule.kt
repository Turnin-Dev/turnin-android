package com.peekr.core.data.source.network.di

import com.peekr.core.data.repository.KeywordRepositoryImpl
import com.peekr.core.data.source.network.api.KeywordApi
import com.peekr.core.data.source.network.datasource.KeywordNetworkDataSource
import com.peekr.core.data.source.network.datasource.KeywordNetworkDataSourceImpl
import com.peekr.core.domain.keyword.repository.KeywordRepository
import dagger.Binds
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

@Module
@InstallIn(SingletonComponent::class)
interface KeywordBindModule {
    @Binds
    fun bindsKeywordNetworkDataSource(impl: KeywordNetworkDataSourceImpl): KeywordNetworkDataSource

    @Binds
    fun bindsKeywordRepository(impl: KeywordRepositoryImpl): KeywordRepository
}
