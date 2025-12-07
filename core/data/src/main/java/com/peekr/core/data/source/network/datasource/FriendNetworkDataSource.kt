package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.FriendApi
import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.util.NetworkResult

interface FriendNetworkDataSource {
    /**
     * 친구 추가
     *
     * @param addFriendRequest 친구 추가 요청 바디
     * @return [FriendResponse] 친구 응답 바디
     * @see FriendApi.addFriend
     */
    suspend fun addFriend(
        addFriendRequest: AddFriendRequest,
    ): NetworkResult<FriendResponse>

    /**
     * 친구 삭제
     *
     * @param deleteFriendRequest 친구 삭제 요청 바디
     * @see FriendApi.deleteFriend
     */
    suspend fun deleteFriend(
        deleteFriendRequest: DeleteFriendRequest,
    ): NetworkResult<Unit>

    /**
     * 친구 관계 상태 수정
     *
     * @param patchFriendStatusRequest 친구 상태 수정 요청 바디
     * @see FriendApi.updateFriendStatus
     */
    suspend fun updateFriendStatus(
        patchFriendStatusRequest: PatchFriendStatusRequest,
    ): NetworkResult<Unit>
}
