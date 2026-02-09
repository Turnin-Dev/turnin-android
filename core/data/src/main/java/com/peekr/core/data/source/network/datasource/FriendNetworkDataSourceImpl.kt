package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.FriendApi
import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.dto.friend.response.FriendsResponse
import com.peekr.core.data.source.network.dto.friend.response.IncomingRequestsResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class FriendNetworkDataSourceImpl @Inject constructor(
    private val friendApi: FriendApi,
) : FriendNetworkDataSource {
    override suspend fun getFriends(
        userId: Long,
        page: Long,
        size: Int,
    ): NetworkResult<FriendsResponse> =
        networkCall { friendApi.getFriends(userId, page, size) }

    override suspend fun getIncomingRequests(
        page: Long,
        size: Int,
    ): NetworkResult<IncomingRequestsResponse> =
        networkCall { friendApi.getIncomingRequests(page, size) }

    override suspend fun addFriend(addFriendRequest: AddFriendRequest): NetworkResult<FriendResponse> =
        networkCall { friendApi.addFriend(addFriendRequest) }

    override suspend fun deleteFriend(deleteFriendRequest: DeleteFriendRequest): NetworkResult<Unit> =
        networkCallWithoutResponse {
            friendApi.deleteFriend(
                requesterId = deleteFriendRequest.requesterId,
                receiverId = deleteFriendRequest.receiverId,
            )
        }

    override suspend fun updateFriendStatus(
        patchFriendStatusRequest: PatchFriendStatusRequest,
    ): NetworkResult<Unit> =
        networkCallWithoutResponse { friendApi.updateFriendStatus(patchFriendStatusRequest) }
}
