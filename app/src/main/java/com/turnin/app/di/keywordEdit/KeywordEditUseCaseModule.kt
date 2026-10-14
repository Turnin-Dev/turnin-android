package com.turnin.app.di.keywordEdit

import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.domain.keywordEdit.usecase.AddUserKeywordUseCase
import com.turnin.domain.keywordEdit.usecase.GetMyKeywordUseCase
import com.turnin.domain.keywordEdit.usecase.KeywordEditUseCases
import com.turnin.domain.keywordEdit.usecase.UpdateUserKeywordUseCase
import com.turnin.domain.keywordEdit.usecase.ValidateKeywordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 키워드 편집 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object KeywordEditUseCaseModule {
    @Provides
    fun provideAddUserKeywordUseCase(
        userRepository: UserRepository,
        userKeywordRepository: UserKeywordRepository,
        logger: DomainLogger,
    ): AddUserKeywordUseCase =
        AddUserKeywordUseCase(
            userRepository = userRepository,
            userKeywordRepository = userKeywordRepository,
            logger = logger,
        )

    @Provides
    fun provideGetMyKeywordUseCase(
        userKeywordRepository: UserKeywordRepository,
        logger: DomainLogger,
    ): GetMyKeywordUseCase =
        GetMyKeywordUseCase(
            userKeywordRepository = userKeywordRepository,
            logger = logger,
        )

    @Provides
    fun provideKeywordEditUseCases(
        add: AddUserKeywordUseCase,
        update: UpdateUserKeywordUseCase,
        getMyKeyword: GetMyKeywordUseCase,
        validateKeyword: ValidateKeywordUseCase,
    ): KeywordEditUseCases =
        KeywordEditUseCases(
            add = add,
            update = update,
            getMyKeyword = getMyKeyword,
            validateKeyword = validateKeyword,
        )

    @Provides
    fun provideUpdateUserKeywordUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): UpdateUserKeywordUseCase =
        UpdateUserKeywordUseCase(
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideValidateKeywordUseCase(): ValidateKeywordUseCase = ValidateKeywordUseCase()
}
