package com.peekr.core.data.di

import com.peekr.core.data.repository.KeywordGraphRepositoryImpl
import com.peekr.core.data.source.network.api.KeywordGraphApi
import com.peekr.core.data.source.network.datasource.KeywordGraphNetworkDataSource
import com.peekr.core.data.source.network.datasource.KeywordGraphNetworkDataSourceImpl
import com.peekr.core.domain.keywordGraph.repository.KeywordGraphRepository
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
class KeywordGraphModule {
    @Provides
    @Singleton
    fun providerKeywordGraphApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): KeywordGraphApi = retrofit
        .client(okHttpClient)
        .build()
        .create(KeywordGraphApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface KeywordGraphBindModule {
    @Binds
    fun bindsKeywordGraphNetworkDataSource(
        impl: KeywordGraphNetworkDataSourceImpl,
    ): KeywordGraphNetworkDataSource

    @Binds
    fun bindsKeywordGraphRepository(
        impl: KeywordGraphRepositoryImpl,
    ): KeywordGraphRepository
}
