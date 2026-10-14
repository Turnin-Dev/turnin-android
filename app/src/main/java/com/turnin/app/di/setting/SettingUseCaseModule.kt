package com.turnin.app.di.setting

import com.turnin.core.domain.auth.repository.AuthRepository
import com.turnin.core.domain.auth.social.SocialAuthManagerFactory
import com.turnin.core.domain.auth.usecase.LogoutUseCase
import com.turnin.core.domain.file.FileRepository
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.domain.setting.usecase.AccountInfoUseCases
import com.turnin.domain.setting.usecase.DeleteAccountUseCase
import com.turnin.domain.setting.usecase.GetAccountInfoUseCase
import com.turnin.domain.setting.usecase.SettingUseCases
import com.turnin.domain.setting.usecase.UpdateAccountInfoUseCase
import com.turnin.domain.setting.usecase.ValidateDisplayIdRemoteUseCase
import com.turnin.domain.setting.usecase.ValidationIntroduceFormatUseCase
import com.turnin.domain.setting.usecase.ValidationNameFormatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 설정 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object SettingUseCaseModule {
    @Provides
    fun provideAccountInfoUseCases(
        updateAccountInfo: UpdateAccountInfoUseCase,
        validateDisplayId: ValidateDisplayIdRemoteUseCase,
        validateName: ValidationNameFormatUseCase,
        validateIntroduce: ValidationIntroduceFormatUseCase,
    ): AccountInfoUseCases =
        AccountInfoUseCases(
            updateAccountInfo = updateAccountInfo,
            validateDisplayId = validateDisplayId,
            validateName = validateName,
            validateIntroduce = validateIntroduce,
        )

    @Provides
    fun provideDeleteAccountUseCase(
        authRepository: AuthRepository,
        notificationRepository: NotificationRepository,
        socialAuthManagerFactory: SocialAuthManagerFactory,
        logger: DomainLogger,
    ): DeleteAccountUseCase =
        DeleteAccountUseCase(
            authRepository = authRepository,
            notificationRepository = notificationRepository,
            socialAuthManagerFactory = socialAuthManagerFactory,
            logger = logger,
        )

    @Provides
    fun provideGetAccountInfoUseCase(
        authRepository: AuthRepository,
        userRepository: UserRepository,
    ): GetAccountInfoUseCase =
        GetAccountInfoUseCase(
            authRepository = authRepository,
            userRepository = userRepository,
        )

    @Provides
    fun provideSettingUseCases(
        logout: LogoutUseCase,
        deleteAccount: DeleteAccountUseCase,
        getAccountInfo: GetAccountInfoUseCase,
    ): SettingUseCases =
        SettingUseCases(
            logout = logout,
            deleteAccount = deleteAccount,
            getAccountInfo = getAccountInfo,
        )

    @Provides
    fun provideUpdateAccountInfoUseCase(
        fileRepository: FileRepository,
        userRepository: UserRepository,
    ): UpdateAccountInfoUseCase =
        UpdateAccountInfoUseCase(
            fileRepository = fileRepository,
            userRepository = userRepository,
        )

    @Provides
    fun provideValidateDisplayIdRemoteUseCase(
        authRepository: AuthRepository,
    ): ValidateDisplayIdRemoteUseCase =
        ValidateDisplayIdRemoteUseCase(
            authRepository = authRepository,
        )

    @Provides
    fun provideValidationIntroduceFormatUseCase(): ValidationIntroduceFormatUseCase = ValidationIntroduceFormatUseCase()

    @Provides
    fun provideValidationNameFormatUseCase(): ValidationNameFormatUseCase = ValidationNameFormatUseCase()
}
