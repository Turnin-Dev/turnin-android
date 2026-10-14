package com.turnin.core.data.di

import com.turnin.core.data.repository.ReportRepositoryImpl
import com.turnin.core.data.source.network.api.ReportApi
import com.turnin.core.data.source.network.datasource.ReportNetworkDataSource
import com.turnin.core.data.source.network.datasource.ReportNetworkDataSourceImpl
import com.turnin.core.domain.report.repository.ReportRepository
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
class ReportModule {
    @Singleton
    @Provides
    fun provideReportApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): ReportApi = retrofit
        .client(okHttpClient)
        .build()
        .create(ReportApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface ReportBindModule {
    @Binds
    fun bindsReportNetworkDataSource(impl: ReportNetworkDataSourceImpl): ReportNetworkDataSource

    @Binds
    fun bindsReportRepository(impl: ReportRepositoryImpl): ReportRepository
}
