package com.turnin.core.data.di

import com.turnin.core.data.repository.FeedRepositoryImpl
import com.turnin.core.data.source.network.api.FeedApi
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSource
import com.turnin.core.data.source.network.datasource.FeedNetworkDataSourceImpl
import com.turnin.core.domain.feed.repository.FeedRepository
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
