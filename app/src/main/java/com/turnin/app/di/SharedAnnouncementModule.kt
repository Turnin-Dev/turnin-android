package com.turnin.app.di

import com.turnin.shared.domain.feature.announcement.usecase.AnnouncementUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.core.Koin

/** 공유 공지 기능을 Hilt에 제공한다. ([SharedKoinBridge]의 Koin 컨테이너에서 꺼낸다.) */
@Module
@InstallIn(SingletonComponent::class)
object SharedAnnouncementModule {
    @Provides
    fun provideAnnouncementUseCases(koin: Koin): AnnouncementUseCases = koin.get()
}
