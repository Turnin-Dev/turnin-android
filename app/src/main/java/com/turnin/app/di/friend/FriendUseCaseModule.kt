package com.turnin.app.di.friend

import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.friend.usecase.AcceptFriendRequestUseCase
import com.turnin.domain.friend.usecase.FriendUseCases
import com.turnin.domain.friend.usecase.GetFriendsUseCase
import com.turnin.domain.friend.usecase.GetIncomingRequestsUseCase
import com.turnin.domain.friend.usecase.GetMyUserIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 친구 UseCase 의존성 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object FriendUseCaseModule {
    @Provides
    fun provideAcceptFriendRequestUseCase(
        friendRepository: FriendRepository,
    ): AcceptFriendRequestUseCase =
        AcceptFriendRequestUseCase(
            friendRepository = friendRepository,
        )

    @Provides
    fun provideFriendUseCases(
        getMyUserId: GetMyUserIdUseCase,
        getFriends: GetFriendsUseCase,
        getIncomingRequests: GetIncomingRequestsUseCase,
        acceptFriendRequest: AcceptFriendRequestUseCase,
    ): FriendUseCases =
        FriendUseCases(
            getMyUserId = getMyUserId,
            getFriends = getFriends,
            getIncomingRequests = getIncomingRequests,
            acceptFriendRequest = acceptFriendRequest,
        )

    @Provides
    fun provideGetFriendsUseCase(
        friendRepository: FriendRepository,
    ): GetFriendsUseCase =
        GetFriendsUseCase(
            friendRepository = friendRepository,
        )

    @Provides
    fun provideGetIncomingRequestsUseCase(
        friendRepository: FriendRepository,
    ): GetIncomingRequestsUseCase =
        GetIncomingRequestsUseCase(
            friendRepository = friendRepository,
        )

    @Provides
    fun provideGetMyUserIdUseCase(
        userRepository: UserRepository,
    ): GetMyUserIdUseCase =
        GetMyUserIdUseCase(
            userRepository = userRepository,
        )
}
