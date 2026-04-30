package com.turnin.core.data.eventBus

import com.turnin.core.domain.eventBus.AuthEventBus
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface EventBusBindModule {
    @Binds
    @Singleton
    fun bindsAuthEventBus(
        impl: AuthEventBusImpl,
    ): AuthEventBus
}
