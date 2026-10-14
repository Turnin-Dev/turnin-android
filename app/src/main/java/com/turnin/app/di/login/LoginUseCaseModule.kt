package com.turnin.app.di.login

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.eventBus.AuthEventBus
import com.turnin.domain.login.usecase.GetSocialLoginResultUseCase
import com.turnin.domain.login.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 로그인 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object LoginUseCaseModule {
    @Provides
    fun provideGetSocialLoginResultUseCase(
        socialAuthManagerFactory: SocialAuthManagerFactory,
        authRepository: AuthRepository,
    ): GetSocialLoginResultUseCase =
        GetSocialLoginResultUseCase(
            socialAuthManagerFactory = socialAuthManagerFactory,
            authRepository = authRepository,
        )

    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        authEventBus: AuthEventBus,
    ): LoginUseCase =
        LoginUseCase(
            authRepository = authRepository,
            authEventBus = authEventBus,
        )
}
