package com.peekr.core.data.source.network.di

import com.peekr.core.data.repository.FileRepositoryImpl
import com.peekr.core.data.source.network.api.FileApi
import com.peekr.core.data.source.network.datasource.FileNetworkDataSource
import com.peekr.core.data.source.network.datasource.FileNetworkDataSourceImpl
import com.peekr.core.domain.file.FileRepository
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
class FileModule {
    @Singleton
    @Provides
    fun provideFileApi(
        retrofit: Retrofit.Builder,
        @DefaultOkHttpClient okHttpClient: OkHttpClient,
    ): FileApi =
        retrofit.client(okHttpClient).build().create(FileApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface FileBindModule {
    @Binds
    fun bindsFileNetworkDataSource(impl: FileNetworkDataSourceImpl): FileNetworkDataSource

    @Binds
    fun bindsFileRepository(impl: FileRepositoryImpl): FileRepository
}
