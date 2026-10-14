package com.turnin.app.di.notification

import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.domain.notification.usecase.GetAnnouncementsUseCase
import com.turnin.domain.notification.usecase.GetNotificationsUseCase
import com.turnin.domain.notification.usecase.MarkAnnouncementAsReadUseCase
import com.turnin.domain.notification.usecase.MarkAsReadUseCase
import com.turnin.domain.notification.usecase.NotificationUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 알림 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationUseCaseModule {
    @Provides
    fun provideGetAnnouncementsUseCase(
        announcementRepository: AnnouncementRepository,
    ): GetAnnouncementsUseCase =
        GetAnnouncementsUseCase(
            announcementRepository = announcementRepository,
        )

    @Provides
    fun provideGetNotificationsUseCase(
        notificationRepository: NotificationRepository,
    ): GetNotificationsUseCase =
        GetNotificationsUseCase(
            notificationRepository = notificationRepository,
        )

    @Provides
    fun provideMarkAnnouncementAsReadUseCase(
        announcementRepository: AnnouncementRepository,
    ): MarkAnnouncementAsReadUseCase =
        MarkAnnouncementAsReadUseCase(
            announcementRepository = announcementRepository,
        )

    @Provides
    fun provideMarkAsReadUseCase(
        notificationRepository: NotificationRepository,
    ): MarkAsReadUseCase =
        MarkAsReadUseCase(
            notificationRepository = notificationRepository,
        )

    @Provides
    fun provideNotificationUseCases(
        getNotifications: GetNotificationsUseCase,
        markAsRead: MarkAsReadUseCase,
        getAnnouncements: GetAnnouncementsUseCase,
        markAnnouncementAsRead: MarkAnnouncementAsReadUseCase,
    ): NotificationUseCases =
        NotificationUseCases(
            getNotifications = getNotifications,
            markAsRead = markAsRead,
            getAnnouncements = getAnnouncements,
            markAnnouncementAsRead = markAnnouncementAsRead,
        )
}
