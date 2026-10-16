package com.turnin.shared.notification

import com.turnin.domain.notification.usecase.GetAnnouncementsUseCase
import com.turnin.domain.notification.usecase.MarkAnnouncementAsReadUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/** 알림(공지) 기능 모듈: UseCase, Facade */
internal val notificationModule: Module = module {
    factory { GetAnnouncementsUseCase(announcementRepository = get()) }
    factory { MarkAnnouncementAsReadUseCase(announcementRepository = get()) }
    factory { AnnouncementFacade(getAnnouncements = get(), markAnnouncementAsRead = get()) }
}
