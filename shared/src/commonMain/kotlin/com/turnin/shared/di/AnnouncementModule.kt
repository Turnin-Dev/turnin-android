package com.turnin.shared.di

import com.turnin.shared.data.feature.announcement.di.announcementDataModule
import com.turnin.shared.domain.feature.announcement.usecase.AnnouncementUseCases
import com.turnin.shared.domain.feature.announcement.usecase.GetAnnouncementsUseCase
import com.turnin.shared.domain.feature.announcement.usecase.MarkAnnouncementAsReadUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/** 공지 기능 모듈: data 모듈 + UseCase (domain은 DI 프레임워크를 모르므로 UseCase 등록은 여기서 한다.) */
internal val announcementModule: Module = module {
    includes(announcementDataModule)

    factory { GetAnnouncementsUseCase(announcementRepository = get()) }
    factory { MarkAnnouncementAsReadUseCase(announcementRepository = get()) }
    factory { AnnouncementUseCases(getAnnouncements = get(), markAnnouncementAsRead = get()) }
}
