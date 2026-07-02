package com.turnin.core.data.di

import com.turnin.core.data.repository.DiscoverRepositoryImpl
import com.turnin.core.data.source.network.api.DiscoverApi
import com.turnin.core.data.source.network.datasource.DiscoverNetworkDataSource
import com.turnin.core.data.source.network.datasource.DiscoverNetworkDataSourceImpl
import com.turnin.core.domain.discover.repository.DiscoverRepository
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
class DiscoverModule {
    @Provides
    @Singleton
    fun provideDiscoverApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): DiscoverApi = retrofit
        .client(okHttpClient)
        .build()
        .create(DiscoverApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface DiscoverBindModule {
    @Binds
    fun bindsDiscoverNetworkDataSource(
        impl: DiscoverNetworkDataSourceImpl,
    ): DiscoverNetworkDataSource

    @Binds
    fun bindsDiscoverRepository(
        impl: DiscoverRepositoryImpl,
    ): DiscoverRepository
}
