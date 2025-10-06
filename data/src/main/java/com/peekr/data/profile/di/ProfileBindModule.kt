package com.peekr.data.profile.di

import com.peekr.data.profile.repository.ProfileRepositoryImpl
import com.peekr.domain.profile.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ProfileBindModule {
    @Binds
    fun bindsProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
