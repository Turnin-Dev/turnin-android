package com.turnin.core.data.di

import com.turnin.core.data.repository.UserRepositoryImpl
import com.turnin.core.data.source.network.api.UserApi
import com.turnin.core.data.source.network.datasource.UserNetworkDataSource
import com.turnin.core.data.source.network.datasource.UserNetworkDataSourceImpl
import com.turnin.core.domain.user.repository.UserRepository
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
class UserModule {
    @Provides
    @Singleton
    fun providerUserApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): UserApi = retrofit
        .client(okHttpClient)
        .build()
        .create(UserApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface UserBindModule {
    @Binds
    @Singleton
    fun bindsUserNetworkDataSource(impl: UserNetworkDataSourceImpl): UserNetworkDataSource

    @Binds
    @Singleton
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}
