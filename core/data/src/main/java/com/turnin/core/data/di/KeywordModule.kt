package com.turnin.core.data.di

import com.turnin.core.data.repository.KeywordRepositoryImpl
import com.turnin.core.data.source.network.api.KeywordApi
import com.turnin.core.data.source.network.datasource.KeywordNetworkDataSource
import com.turnin.core.data.source.network.datasource.KeywordNetworkDataSourceImpl
import com.turnin.core.domain.keyword.repository.KeywordRepository
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
