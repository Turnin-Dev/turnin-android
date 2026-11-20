package com.turnin.core.data.di

import com.turnin.core.data.repository.ContentReportRepositoryImpl
import com.turnin.core.data.source.network.api.ContentReportApi
import com.turnin.core.data.source.network.datasource.ContentReportNetworkDataSource
import com.turnin.core.data.source.network.datasource.ContentReportNetworkDataSourceImpl
import com.turnin.core.domain.contentReport.repository.ContentReportRepository
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
class ContentReportModule {
    @Singleton
    @Provides
    fun provideContentReportApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): ContentReportApi = retrofit
        .client(okHttpClient)
        .build()
        .create(ContentReportApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface ContentReportBindModule {
    @Binds
    fun bindsContentReportNetworkDataSource(
        impl: ContentReportNetworkDataSourceImpl,
    ): ContentReportNetworkDataSource

    @Binds
    fun bindsContentReportRepository(
        impl: ContentReportRepositoryImpl,
    ): ContentReportRepository
}
