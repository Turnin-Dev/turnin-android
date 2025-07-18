package com.peekr.data.shared.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class CoroutineModule {
    /**
     * IO 작업에 최적화된 코루틴 디스패처를 싱글톤으로 제공합니다.
     *
     * @return IO 바운드 작업에 적합한 CoroutineDispatcher 인스턴스
     */
    @IO
    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * CPU 집약적인 작업을 위한 기본 CoroutineDispatcher를 싱글톤으로 제공합니다.
     *
     * @return 기본 디스패처(Dispatchers.Default) 인스턴스
     */
    @Default
    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * 기본 디스패처와 SupervisorJob을 결합한 싱글톤 CoroutineScope를 제공합니다.
     *
     * @param dispatcher 기본(@Default) CoroutineDispatcher가 주입됩니다.
     * @return SupervisorJob과 결합된 CoroutineScope 인스턴스입니다.
     */
    @Provides
    @Singleton
    fun provideCoroutineScope(
        @Default dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class IO

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Default
