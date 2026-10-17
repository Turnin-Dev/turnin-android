package com.turnin.shared.feature.announcement.di

import com.turnin.shared.di.PlatformQualifier
import com.turnin.shared.feature.announcement.data.AnnouncementNetworkDataSource
import com.turnin.shared.feature.announcement.data.AnnouncementNetworkDataSourceImpl
import com.turnin.shared.feature.announcement.data.AnnouncementRepository
import com.turnin.shared.feature.announcement.data.AnnouncementRepositoryImpl
import com.turnin.shared.feature.announcement.usecase.AnnouncementUseCases
import com.turnin.shared.feature.announcement.usecase.GetAnnouncementsUseCase
import com.turnin.shared.feature.announcement.usecase.MarkAnnouncementAsReadUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

/** 공지 기능 모듈: DataSource, Repository, UseCase */
internal val announcementModule: Module = module {
    single<AnnouncementNetworkDataSource> {
        AnnouncementNetworkDataSourceImpl(httpClient = get(PlatformQualifier.AuthHttpClient))
    }
    single<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            announcementNetworkDataSource = get(),
            ioDispatcher = get(PlatformQualifier.IoDispatcher),
        )
    }
    factory { GetAnnouncementsUseCase(announcementRepository = get()) }
    factory { MarkAnnouncementAsReadUseCase(announcementRepository = get()) }
    factory { AnnouncementUseCases(getAnnouncements = get(), markAnnouncementAsRead = get()) }
}
