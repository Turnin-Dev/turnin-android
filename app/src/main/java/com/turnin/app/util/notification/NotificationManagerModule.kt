package com.turnin.app.util.notification

import com.turnin.core.domain.notification.NotificationSyncManager
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
        impl: com.turnin.app.util.notification.NotificationSyncManagerImpl,
    ): NotificationSyncManager
}
