package com.peekr.core.data.di

import com.peekr.core.data.repository.SettingRepositoryImpl
import com.peekr.core.domain.setting.repository.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SettingModule {
    @Binds
    @Singleton
    fun bindsSettingRepository(
        impl: SettingRepositoryImpl,
    ): SettingRepository
}
