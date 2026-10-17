package com.turnin.shared.data.feature.announcement.di

import com.turnin.shared.data.core.di.SharedQualifier
import com.turnin.shared.data.feature.announcement.datasource.AnnouncementNetworkDataSource
import com.turnin.shared.data.feature.announcement.datasource.AnnouncementNetworkDataSourceImpl
import com.turnin.shared.data.feature.announcement.repository.AnnouncementRepositoryImpl
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import org.koin.core.module.Module
import org.koin.dsl.module

/** 공지 data 모듈: DataSource, Repository (구현은 이 모듈 밖에 노출하지 않는다.) */
val announcementDataModule: Module = module {
    single<AnnouncementNetworkDataSource> {
        AnnouncementNetworkDataSourceImpl(httpClient = get(SharedQualifier.AuthHttpClient))
    }
    single<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            announcementNetworkDataSource = get(),
            ioDispatcher = get(SharedQualifier.IoDispatcher),
        )
    }
}
