package com.peekr.core.data.repository

import com.peekr.core.common.coroutine.IO
import com.peekr.core.data.source.network.datasource.FriendNetworkDataSource
import com.peekr.core.data.source.network.dto.friend.request.toDataModel
import com.peekr.core.data.source.network.dto.friend.response.toDomainModel
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.coroutine.safeResultFlow
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.AddFriend
import com.peekr.core.domain.friend.model.DeleteFriend
import com.peekr.core.domain.friend.model.Friend
import com.peekr.core.domain.friend.model.PatchFriendshipStatus
import com.peekr.core.domain.friend.repository.FriendRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class FriendRepositoryImpl @Inject constructor(
    private val friendNetworkDataSource: FriendNetworkDataSource,
    @IO private val ioDispatcher: CoroutineDispatcher,
) : FriendRepository {
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

    override fun updateFriendshipStatus(
        patchFriendshipStatus: PatchFriendshipStatus,
    ): Flow<Result<Unit, CommonErrorType>> =
        safeResultFlow<Unit, CommonErrorType>(ioDispatcher, { CommonErrorType.Unexpected(it) }) {
            emit(Result.Loading)
            when (
                val result =
                    friendNetworkDataSource.updateFriendshipStatus(patchFriendshipStatus.toDataModel())
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
