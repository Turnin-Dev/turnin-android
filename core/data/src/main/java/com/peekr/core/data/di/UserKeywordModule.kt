package com.peekr.core.data.di

import com.peekr.core.data.repository.UserKeywordRepositoryImpl
import com.peekr.core.data.source.network.api.UserKeywordApi
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSourceImpl
import com.peekr.core.domain.userKeyword.repository.UserKeywordRepository
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
class UserKeywordModule {
    @Singleton
    @Provides
    fun providerUserKeywordApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): UserKeywordApi = retrofit
        .client(okHttpClient)
        .build()
        .create(UserKeywordApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface UserKeywordBindModule {
    @Binds
    @Singleton
    fun bindsUserKeywordNetworkDataSource(
        impl: UserKeywordNetworkDataSourceImpl,
    ): UserKeywordNetworkDataSource

    @Binds
    @Singleton
    fun bindsUserKeywordRepository(impl: UserKeywordRepositoryImpl): UserKeywordRepository
}
