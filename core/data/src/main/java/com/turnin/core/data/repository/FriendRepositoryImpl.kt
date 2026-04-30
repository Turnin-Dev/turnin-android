package com.turnin.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.paging.TurninPagingSource
import com.turnin.core.data.source.local.memory.MemoryCache
import com.turnin.core.data.source.network.datasource.FriendNetworkDataSource
import com.turnin.core.data.source.network.dto.friend.request.toDataModel
import com.turnin.core.data.source.network.dto.friend.response.FriendInfoResponse
import com.turnin.core.data.source.network.dto.friend.response.IncomingRequestResponse
import com.turnin.core.data.source.network.dto.friend.response.toDomainModel
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.coroutine.safeResultFlow
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.friend.model.AddFriend
import com.turnin.core.domain.friend.model.DeleteFriend
import com.turnin.core.domain.friend.model.Friend
import com.turnin.core.domain.friend.model.FriendInfo
import com.turnin.core.domain.friend.model.FriendPagingTokens
import com.turnin.core.domain.friend.model.IncomingRequest
import com.turnin.core.domain.friend.model.IncomingRequestPagingTokens
import com.turnin.core.domain.friend.model.PatchFriendStatus
import com.turnin.core.domain.friend.repository.FriendRepository
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreUserProfile
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FriendRepositoryImpl @Inject constructor(
    private val friendNetworkDataSource: FriendNetworkDataSource,
    private val memoryCache: MemoryCache<UserId, CoreUserProfile>,
    @param:IO private val ioDispatcher: CoroutineDispatcher,
) : FriendRepository {
    override fun getFriends(userId: UserId): Flow<PagingData<FriendInfo>> {
        val pageSize = FriendPagingTokens.PAGE_SIZE
        val prefetchDistance = FriendPagingTokens.PREFETCH_DISTANCE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                initialLoadSize = pageSize + prefetchDistance,
            ),
            pagingSourceFactory = {
                TurninPagingSource(
                    apiCall = { page ->
                        friendNetworkDataSource.getFriends(userId.value, page, pageSize)
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(FriendInfoResponse::toDomainModel)
            }
    }

    override fun getIncomingRequests(): Flow<PagingData<IncomingRequest>> {
        val pageSize = IncomingRequestPagingTokens.PAGE_SIZE
        val prefetchDistance = IncomingRequestPagingTokens.PREFETCH_DISTANCE

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance,
                initialLoadSize = pageSize + prefetchDistance,
            ),
            pagingSourceFactory = {
                TurninPagingSource(
                    apiCall = { page ->
                        friendNetworkDataSource.getIncomingRequests(page, pageSize)
                    },
                )
            },
        )
            .flow
            .map { pagingData ->
                pagingData.map(IncomingRequestResponse::toDomainModel)
            }
    }

    override fun addFriend(addFriend: AddFriend): Flow<Result<Friend, CommonErrorType>> =
        safeResultFlow<Friend, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 사용자 프로필 관련 액션 수행 시 메모리 캐시 무효화
            memoryCache.remove(addFriend.receiverId)

            // 네트워크 호출
            when (val result = friendNetworkDataSource.addFriend(addFriend.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(result.data.toDomainModel()))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun deleteFriend(deleteFriend: DeleteFriend): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 사용자 프로필 관련 액션 수행 시 메모리 캐시 무효화
            memoryCache.remove(deleteFriend.receiverId)

            // 네트워크 호출
            when (val result = friendNetworkDataSource.deleteFriend(deleteFriend.toDataModel())) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }

    override fun updateFriendStatus(
        patchFriendStatus: PatchFriendStatus,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)

            // 사용자 프로필 관련 액션 수행 시 메모리 캐시 무효화
            memoryCache.remove(patchFriendStatus.receiverId)

            // 네트워크 호출
            when (
                val result =
                    friendNetworkDataSource.updateFriendStatus(patchFriendStatus.toDataModel())
            ) {
                is NetworkResult.Success -> {
                    emit(Result.Success(Unit))
                }

                is NetworkResult.Error -> {
                    val error = result.error.toCommonErrorType()
                    emit(Result.Error(error = error, message = result.message))
                }
            }
        }
}
