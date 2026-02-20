package com.peekr.core.data.di

import com.peekr.core.data.repository.FeedRepositoryImpl
import com.peekr.core.data.source.network.api.FeedApi
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSource
import com.peekr.core.data.source.network.datasource.FeedNetworkDataSourceImpl
import com.peekr.core.domain.feed.repository.FeedRepository
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
class FeedModule {
    @Provides
    @Singleton
    fun provideFeedApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): FeedApi = retrofit
        .client(okHttpClient)
        .build()
        .create(FeedApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface FeedBindModule {
    @Binds
    @Singleton
    fun bindsFeedNetworkDataSource(
        impl: FeedNetworkDataSourceImpl,
    ): FeedNetworkDataSource

    @Binds
    @Singleton
    fun bindsFeedRepository(
        impl: FeedRepositoryImpl,
    ): FeedRepository
}
