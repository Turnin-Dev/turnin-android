package com.peekr.peekrapp.util.notification

import com.peekr.core.domain.notification.NotificationSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NotificationManagerModule {
    @Binds
    @Singleton
    fun bindNotificationManager(
        impl: NotificationSyncManagerImpl,
    ): NotificationSyncManager
}
