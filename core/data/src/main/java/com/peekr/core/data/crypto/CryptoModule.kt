package com.peekr.core.data.crypto

import com.peekr.core.common.coroutine.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {
    @Singleton
    @Provides
    fun provideCryptoManager(
        @IO ioDispatcher: CoroutineDispatcher,
    ): CryptoManager = CryptoManager(ioDispatcher)
}
