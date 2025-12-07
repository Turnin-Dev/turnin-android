package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.FriendApi
import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendshipStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class FriendNetworkDataSourceImpl @Inject constructor(
    private val friendApi: FriendApi,
) : FriendNetworkDataSource {
    override suspend fun addFriend(addFriendRequest: AddFriendRequest): NetworkResult<FriendResponse> =
        networkCall { friendApi.addFriend(addFriendRequest) }

    override suspend fun deleteFriend(deleteFriendRequest: DeleteFriendRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { friendApi.deleteFriend(deleteFriendRequest) }

    override suspend fun updateFriendshipStatus(
        patchFriendshipStatusRequest: PatchFriendshipStatusRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse { friendApi.updateFriendshipStatus(patchFriendshipStatusRequest) }
}
