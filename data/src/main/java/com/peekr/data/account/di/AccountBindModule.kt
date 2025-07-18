package com.peekr.data.account.di

import com.peekr.data.account.network.AccountNetworkDataSource
import com.peekr.data.account.network.AccountNetworkDataSourceImpl
import com.peekr.data.account.repository.AccountRepositoryImpl
import com.peekr.data.account.util.AuthManagerFactoryImpl
import com.peekr.domain.account.repository.AccountRepository
import com.peekr.domain.account.util.AuthManagerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AccountBindModule {
    /**
     * AuthManagerFactory 인터페이스에 대한 구현체로 AuthManagerFactoryImpl을 바인딩합니다.
     *
     * 의존성 주입 시 AuthManagerFactory가 필요할 때 AuthManagerFactoryImpl이 제공됩니다.
     */
    @Binds
    fun bindsAuthManagerFactory(impl: AuthManagerFactoryImpl): AuthManagerFactory

    /**
     * `AccountNetworkDataSource` 인터페이스에 대한 구현체로 `AccountNetworkDataSourceImpl`을 바인딩합니다.
     *
     * 의존성 주입 시 `AccountNetworkDataSource` 타입이 요청되면 `AccountNetworkDataSourceImpl` 인스턴스가 제공됩니다.
     */
    @Binds
    fun bindsAccountNetworkDataSource(impl: AccountNetworkDataSourceImpl): AccountNetworkDataSource

    /**
     * `AccountRepository` 인터페이스에 대한 구현체로 `AccountRepositoryImpl`을 바인딩합니다.
     *
     * 의존성 주입 시 `AccountRepository`가 요구될 때 `AccountRepositoryImpl`이 제공됩니다.
     */
    @Binds
    fun bindsAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
