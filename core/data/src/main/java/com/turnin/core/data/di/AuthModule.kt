package com.turnin.core.data.di

import com.turnin.core.data.repository.AuthRepositoryImpl
import com.turnin.core.data.source.network.api.AuthApi
import com.turnin.core.data.source.network.datasource.AuthNetworkDataSource
import com.turnin.core.data.source.network.datasource.AuthNetworkDataSourceImpl
import com.turnin.core.domain.auth.repository.AuthRepository
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
class AuthModule {
    @Singleton
    @Provides
    fun provideAuthApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): AuthApi =
        retrofit.client(okHttpClient).build().create(AuthApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface AuthBindModule {
    @Binds
    @Singleton
    fun bindsAuthNetworkDataSource(impl: AuthNetworkDataSourceImpl): AuthNetworkDataSource

    @Binds
    @Singleton
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
