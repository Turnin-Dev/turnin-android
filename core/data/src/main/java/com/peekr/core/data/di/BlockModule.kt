package com.peekr.core.data.di

import com.peekr.core.data.repository.BlockRepositoryImpl
import com.peekr.core.data.source.network.api.BlockApi
import com.peekr.core.data.source.network.datasource.BlockNetworkDataSource
import com.peekr.core.data.source.network.datasource.BlockNetworkDataSourceImpl
import com.peekr.core.domain.block.repository.BlockRepository
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
