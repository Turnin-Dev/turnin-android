package com.turnin.core.data.di

import com.turnin.core.data.source.network.api.AccountApi
import com.turnin.core.data.source.network.datasource.AccountNetworkDataSource
import com.turnin.core.data.source.network.datasource.AccountNetworkDataSourceImpl
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
class AccountModule {
    @Singleton
    @Provides
    fun provideAccountApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): AccountApi = retrofit
        .client(okHttpClient)
        .build()
        .create(AccountApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface AccountBindModule {
    @Binds
    @Singleton
    fun bindsAccountNetworkDataSource(
        impl: AccountNetworkDataSourceImpl,
    ): AccountNetworkDataSource
}
