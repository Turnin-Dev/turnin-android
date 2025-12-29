package com.peekr.core.data.di

import com.peekr.core.data.repository.FriendRepositoryImpl
import com.peekr.core.data.source.network.api.FriendApi
import com.peekr.core.data.source.network.datasource.FriendNetworkDataSource
import com.peekr.core.data.source.network.datasource.FriendNetworkDataSourceImpl
import com.peekr.core.domain.friend.repository.FriendRepository
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
class FriendModule {
    @Provides
    @Singleton
    fun provideFriendApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): FriendApi = retrofit
        .client(okHttpClient)
        .build()
        .create(FriendApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface FriendBindModule {
    @Binds
    fun bindsFriendNetworkDataSource(
        impl: FriendNetworkDataSourceImpl,
    ): FriendNetworkDataSource

    @Binds
    fun bindsFriendRepository(
        impl: FriendRepositoryImpl,
    ): FriendRepository
}
