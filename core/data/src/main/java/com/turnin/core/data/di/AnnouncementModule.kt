package com.turnin.core.data.di

import com.turnin.core.data.repository.AnnouncementRepositoryImpl
import com.turnin.core.data.source.network.api.AnnouncementApi
import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSource
import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSourceImpl
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
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
class AnnouncementModule {
    @Singleton
    @Provides
    fun provideAnnouncementApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): AnnouncementApi = retrofit
        .client(okHttpClient)
        .build()
        .create(AnnouncementApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface AnnouncementBindModule {
    @Binds
    @Singleton
    fun bindsAnnounceNetworkDataSource(
        impl: AnnouncementNetworkDataSourceImpl,
    ): AnnouncementNetworkDataSource

    @Binds
    @Singleton
    fun bindsAnnouncementRepository(
        impl: AnnouncementRepositoryImpl,
    ): AnnouncementRepository
}
