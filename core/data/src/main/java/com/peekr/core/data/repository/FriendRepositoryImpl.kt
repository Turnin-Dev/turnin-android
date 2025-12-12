package com.peekr.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.peekr.core.common.coroutine.IO
import com.peekr.core.common.logger.AppLogger
import com.peekr.core.data.paging.PeekrPagingSource
import com.peekr.core.data.source.network.datasource.FriendNetworkDataSource
import com.peekr.core.data.source.network.dto.friend.request.toDataModel
import com.peekr.core.data.source.network.dto.friend.response.FriendInfoResponse
import com.peekr.core.data.source.network.dto.friend.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.Friend
import com.peekr.core.domain.friend.model.FriendInfo
import com.peekr.core.domain.friend.model.FriendPagingTokens
import com.peekr.core.domain.friend.model.PatchFriendStatus
import com.peekr.core.domain.friend.repository.FriendRepository
import com.peekr.core.domain.model.UserId
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class FriendRepositoryImpl @Inject constructor(
    private val friendNetworkDataSource: FriendNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : FriendRepository {
    private val tag = this::class.java.simpleName

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
                PeekrPagingSource(
                    apiCall = { page ->
                        friendNetworkDataSource.getFriends(userId.value, page, pageSize)
                    },
                )
            },
        )
            .flow
            .map { pagingData -> pagingData.map(FriendInfoResponse::toDomainModel) }
            .catch { e ->
                AppLogger.d(tag, e, "Unexpected friend pagination error")
                emit(PagingData.empty())
            }
    }

    override fun addFriend(addFriend: AddFriend): Flow<Result<Friend, CommonErrorType>> =
        safeResultFlow<Friend, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
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
