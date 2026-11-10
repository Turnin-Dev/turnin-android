package com.turnin.core.data.di

import com.turnin.core.data.repository.PingPongRepositoryImpl
import com.turnin.core.data.source.network.api.PingPongApi
import com.turnin.core.data.source.network.datasource.PingPongNetworkDataSource
import com.turnin.core.data.source.network.datasource.PingPongNetworkDataSourceImpl
import com.turnin.core.domain.pingPong.repository.PingPongRepository
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
class PingPongModule {
    @Provides
    @Singleton
    fun providePingPongApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): PingPongApi = retrofit
        .client(okHttpClient)
        .build()
        .create(PingPongApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface PingPongBindModule {
    @Binds
    fun bindsPingPongNetworkDataSource(
        impl: PingPongNetworkDataSourceImpl,
    ): PingPongNetworkDataSource

    @Binds
    fun bindsPingPongRepository(
        impl: PingPongRepositoryImpl,
    ): PingPongRepository
}
