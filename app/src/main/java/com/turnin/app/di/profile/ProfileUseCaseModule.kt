package com.turnin.app.di.profile

import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.core.domain.userKeyword.repository.UserKeywordRepository
import com.turnin.domain.profile.usecase.MyProfileUseCases
import com.turnin.domain.profile.usecase.UserProfileUseCases
import com.turnin.domain.profile.usecase.my.GetCurrentMyProfileUseCase
import com.turnin.domain.profile.usecase.my.GetMyKeywordsUseCase
import com.turnin.domain.profile.usecase.my.GetMyProfileUseCase
import com.turnin.domain.profile.usecase.my.RefreshMyKeywordsUseCase
import com.turnin.domain.profile.usecase.my.RefreshMyProfileUseCase
import com.turnin.domain.profile.usecase.user.DeleteBlockUseCase
import com.turnin.domain.profile.usecase.user.GetCachedUserProfileUseCase
import com.turnin.domain.profile.usecase.user.GetUserKeywordsUseCase
import com.turnin.domain.profile.usecase.user.GetUserProfileUseCase
import com.turnin.domain.profile.usecase.user.UpdateFriendStateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 프로필 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object ProfileUseCaseModule {
    @Provides
    fun provideMyProfileUseCases(
        getCurrentMyProfile: GetCurrentMyProfileUseCase,
        getMyProfile: GetMyProfileUseCase,
        refreshMyProfile: RefreshMyProfileUseCase,
        getMyKeywords: GetMyKeywordsUseCase,
        refreshMyKeywords: RefreshMyKeywordsUseCase,
    ): MyProfileUseCases =
        MyProfileUseCases(
            getCurrentMyProfile = getCurrentMyProfile,
            getMyProfile = getMyProfile,
            refreshMyProfile = refreshMyProfile,
            getMyKeywords = getMyKeywords,
            refreshMyKeywords = refreshMyKeywords,
        )

    @Provides
    fun provideUserProfileUseCases(
        getUserProfile: GetUserProfileUseCase,
        getCachedUserProfile: GetCachedUserProfileUseCase,
        getUserKeywords: GetUserKeywordsUseCase,
        updateFriendStatus: UpdateFriendStateUseCase,
        deleteBlock: DeleteBlockUseCase,
    ): UserProfileUseCases =
        UserProfileUseCases(
            getUserProfile = getUserProfile,
            getCachedUserProfile = getCachedUserProfile,
            getUserKeywords = getUserKeywords,
            updateFriendStatus = updateFriendStatus,
            deleteBlock = deleteBlock,
        )

    @Provides
    fun provideGetCurrentMyProfileUseCase(
        userRepository: UserRepository,
    ): GetCurrentMyProfileUseCase =
        GetCurrentMyProfileUseCase(
            userRepository = userRepository,
        )

    @Provides
    fun provideGetMyKeywordsUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): GetMyKeywordsUseCase =
        GetMyKeywordsUseCase(
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideGetMyProfileUseCase(
        userRepository: UserRepository,
    ): GetMyProfileUseCase =
        GetMyProfileUseCase(
            userRepository = userRepository,
        )

    @Provides
    fun provideRefreshMyKeywordsUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): RefreshMyKeywordsUseCase =
        RefreshMyKeywordsUseCase(
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideRefreshMyProfileUseCase(
        userRepository: UserRepository,
    ): RefreshMyProfileUseCase =
        RefreshMyProfileUseCase(
            userRepository = userRepository,
        )

    @Provides
    fun provideDeleteBlockUseCase(
        blockRepository: BlockRepository,
    ): DeleteBlockUseCase =
        DeleteBlockUseCase(
            blockRepository = blockRepository,
        )

    @Provides
    fun provideGetCachedUserProfileUseCase(
        userRepository: UserRepository,
    ): GetCachedUserProfileUseCase =
        GetCachedUserProfileUseCase(
            userRepository = userRepository,
        )

    @Provides
    fun provideGetUserKeywordsUseCase(
        userKeywordRepository: UserKeywordRepository,
    ): GetUserKeywordsUseCase =
        GetUserKeywordsUseCase(
            userKeywordRepository = userKeywordRepository,
        )

    @Provides
    fun provideGetUserProfileUseCase(
        userRepository: UserRepository,
    ): GetUserProfileUseCase =
        GetUserProfileUseCase(
            userRepository = userRepository,
        )

    @Provides
    fun provideUpdateFriendStateUseCase(
        userRepository: UserRepository,
        friendRepository: FriendRepository,
    ): UpdateFriendStateUseCase =
        UpdateFriendStateUseCase(
            userRepository = userRepository,
            friendRepository = friendRepository,
        )
}
