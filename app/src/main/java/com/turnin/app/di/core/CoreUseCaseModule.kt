package com.turnin.app.di.core

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.auth.usecase.LogoutUseCase
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.user.usecase.GetMyUserIdUseCase
import com.turnin.core.domain.util.DomainLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 공통(core:domain) UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object CoreUseCaseModule {
    @Provides
    fun provideLogoutUseCase(
        authRepository: AuthRepository,
        notificationRepository: NotificationRepository,
        socialAuthManagerFactory: SocialAuthManagerFactory,
        logger: DomainLogger,
    ): LogoutUseCase =
        LogoutUseCase(
            authRepository = authRepository,
            notificationRepository = notificationRepository,
            socialAuthManagerFactory = socialAuthManagerFactory,
            logger = logger,
        )

    @Provides
    fun provideGetMyUserIdUseCase(
        userRepository: UserRepository,
        logger: DomainLogger,
    ): GetMyUserIdUseCase =
        GetMyUserIdUseCase(
            userRepository = userRepository,
            logger = logger,
        )
}
