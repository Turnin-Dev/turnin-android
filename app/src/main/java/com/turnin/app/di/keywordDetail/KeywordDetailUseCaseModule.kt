package com.turnin.app.di.keywordDetail

import com.turnin.core.domain.user.usecase.GetMyUserIdUseCase
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.core.domain.util.DomainLogger
import com.turnin.domain.keywordDetail.usecase.DeleteKeywordUseCase
import com.turnin.domain.keywordDetail.usecase.GetKeywordDetailUseCase
import com.turnin.domain.keywordDetail.usecase.KeywordDetailUseCases
import com.turnin.domain.keywordDetail.usecase.RefreshKeywordDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 키워드 상세 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object KeywordDetailUseCaseModule {
    @Provides
    fun provideDeleteKeywordUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): DeleteKeywordUseCase =
        DeleteKeywordUseCase(
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideGetKeywordDetailUseCase(
        userKeywordRepository: UserKeywordRepository,
        getMyUserIdUseCase: GetMyUserIdUseCase,
        logger: DomainLogger,
    ): GetKeywordDetailUseCase =
        GetKeywordDetailUseCase(
            userKeywordRepository = userKeywordRepository,
            getMyUserIdUseCase = getMyUserIdUseCase,
            logger = logger,
        )

    @Provides
    fun provideKeywordDetailUseCases(
        getKeywordDetail: GetKeywordDetailUseCase,
        refreshKeywordDetail: RefreshKeywordDetailUseCase,
        deleteKeyword: DeleteKeywordUseCase,
    ): KeywordDetailUseCases =
        KeywordDetailUseCases(
            getKeywordDetail = getKeywordDetail,
            refreshKeywordDetail = refreshKeywordDetail,
            deleteKeyword = deleteKeyword,
        )

    @Provides
    fun provideRefreshKeywordDetailUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): RefreshKeywordDetailUseCase =
        RefreshKeywordDetailUseCase(
            userKeywordRepository = userKeywordRepository,
        )
}
