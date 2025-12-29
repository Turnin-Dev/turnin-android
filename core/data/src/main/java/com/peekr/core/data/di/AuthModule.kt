package com.peekr.core.data.di

import com.peekr.core.data.repository.AuthRepositoryImpl
import com.peekr.core.data.source.network.api.AuthApi
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSource
import com.peekr.core.data.source.network.datasource.AuthNetworkDataSourceImpl
import com.peekr.core.domain.auth.repository.AuthRepository
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
    fun bindsAuthNetworkDataSource(impl: AuthNetworkDataSourceImpl): AuthNetworkDataSource

    @Binds
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
