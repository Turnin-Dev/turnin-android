package com.peekr.core.data.file.di

import com.peekr.core.data.file.network.FileDataSource
import com.peekr.core.data.file.network.FileNetworkDataSource
import com.peekr.core.data.file.repository.FileRepositoryImpl
import com.peekr.core.domain.file.FileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FileBindModule {
    @Binds
    fun bindsFileDataSource(impl: FileNetworkDataSource): FileDataSource

    @Binds
    fun bindsFileRepository(impl: FileRepositoryImpl): FileRepository
}
