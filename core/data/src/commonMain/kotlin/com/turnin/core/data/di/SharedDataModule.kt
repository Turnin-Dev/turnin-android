package com.turnin.core.data.di

import com.turnin.core.data.repository.AnnouncementRepositoryImpl
import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSource
import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSourceImpl
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import org.koin.dsl.module

/**
 * KMP 공유 data 객체 Koin 모듈
 *
 * 플랫폼 재료([SharedQualifier])는 플랫폼 모듈에서 제공해야 한다.
 * (Android: `SharedDataKoin`을 통해 Hilt에서 주입, iOS: `:shared`에서 직접 제공)
 */
val sharedDataModule = module {
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
