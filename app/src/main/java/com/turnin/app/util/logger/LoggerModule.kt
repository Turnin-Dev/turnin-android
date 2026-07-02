package com.turnin.app.util.logger

import com.turnin.core.domain.util.DomainLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LoggerModule {
    @Binds
    @Singleton
    fun bindDomainLogger(
        domainLoggerImpl: com.turnin.app.util.logger.DomainLoggerImpl,
    ): DomainLogger
}
