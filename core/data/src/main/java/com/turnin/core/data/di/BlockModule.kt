package com.turnin.core.data.di

import com.turnin.core.data.repository.BlockRepositoryImpl
import com.turnin.core.data.source.network.api.BlockApi
import com.turnin.core.data.source.network.datasource.BlockNetworkDataSource
import com.turnin.core.data.source.network.datasource.BlockNetworkDataSourceImpl
import com.turnin.core.domain.block.repository.BlockRepository
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
class BlockModule {
    @Provides
    @Singleton
    fun provideBlockApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): BlockApi = retrofit
        .client(okHttpClient)
        .build()
        .create(BlockApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface BlockBindModule {
    @Binds
    fun bindsBlockNetworkDataSource(
        impl: BlockNetworkDataSourceImpl,
    ): BlockNetworkDataSource

    @Binds
    fun bindsBlockRepository(
        impl: BlockRepositoryImpl,
    ): BlockRepository
}
