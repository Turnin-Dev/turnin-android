package com.turnin.app.di.block

import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.block.usecase.CreateBlockUseCase
import com.turnin.domain.block.usecase.DeleteBlockUseCase
import com.turnin.domain.block.usecase.GetBlockReasonsUseCase
import com.turnin.domain.block.usecase.GetBlockedUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 차단 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object BlockUseCaseModule {
    @Provides
    fun provideCreateBlockUseCase(
        userRepository: UserRepository,
        blockRepository: BlockRepository,
    ): CreateBlockUseCase =
        CreateBlockUseCase(
            userRepository = userRepository,
            blockRepository = blockRepository,
        )

    @Provides
    fun provideDeleteBlockUseCase(
        blockRepository: BlockRepository,
    ): DeleteBlockUseCase =
        DeleteBlockUseCase(
            blockRepository = blockRepository,
        )

    @Provides
    fun provideGetBlockReasonsUseCase(
        blockRepository: BlockRepository,
    ): GetBlockReasonsUseCase =
        GetBlockReasonsUseCase(
            blockRepository = blockRepository,
        )

    @Provides
    fun provideGetBlockedUsersUseCase(
        blockRepository: BlockRepository,
    ): GetBlockedUsersUseCase =
        GetBlockedUsersUseCase(
            blockRepository = blockRepository,
        )
}
