package com.peekr.data.notification.di

import com.peekr.core.data.di.TokenOkHttpClient
import com.peekr.data.notification.NotificationApi
import com.peekr.data.notification.datasource.NotificationNetworkDataSource
import com.peekr.data.notification.datasource.NotificationNetworkDataSourceImpl
import com.peekr.data.notification.repository.NotificationRepositoryImpl
import com.peekr.domain.notification.repository.NotificationRepository
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
class NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationApi(
        retrofit: Retrofit.Builder,
        @TokenOkHttpClient okHttpClient: OkHttpClient,
    ): NotificationApi = retrofit
        .client(okHttpClient)
        .build()
        .create(NotificationApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface NotificationBindModule {
    @Binds
    fun bindsNotificationNetworkDataSource(
        impl: NotificationNetworkDataSourceImpl,
    ): NotificationNetworkDataSource

    @Binds
    fun bindsNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository
}
