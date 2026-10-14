package com.turnin.app.di.register

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.eventBus.AuthEventBus
import com.turnin.core.domain.file.FileRepository
import com.turnin.domain.register.usecase.CheckDisplayIdExistsUseCase
import com.turnin.domain.register.usecase.RegisterIntegrationUseCase
import com.turnin.domain.register.usecase.ValidateDisplayIdUseCase
import com.turnin.domain.register.usecase.ValidateIntroduceUseCase
import com.turnin.domain.register.usecase.ValidateNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 회원가입 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object RegisterUseCaseModule {
    @Provides
    fun provideCheckDisplayIdExistsUseCase(
        authRepository: AuthRepository,
    ): CheckDisplayIdExistsUseCase =
        CheckDisplayIdExistsUseCase(
            authRepository = authRepository,
        )

    @Provides
    fun provideRegisterIntegrationUseCase(
        authRepository: AuthRepository,
        fileRepository: FileRepository,
        authEventBus: AuthEventBus,
    ): RegisterIntegrationUseCase =
        RegisterIntegrationUseCase(
            authRepository = authRepository,
            fileRepository = fileRepository,
            authEventBus = authEventBus,
        )

    @Provides
    fun provideValidateDisplayIdUseCase(): ValidateDisplayIdUseCase = ValidateDisplayIdUseCase()

    @Provides
    fun provideValidateIntroduceUseCase(): ValidateIntroduceUseCase = ValidateIntroduceUseCase()

    @Provides
    fun provideValidateNameUseCase(): ValidateNameUseCase = ValidateNameUseCase()
}
