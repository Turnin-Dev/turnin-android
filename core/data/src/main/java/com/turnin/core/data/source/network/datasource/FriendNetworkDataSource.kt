package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.api.FriendApi
import com.turnin.core.data.source.network.dto.friend.request.AddFriendRequest
import com.turnin.core.data.source.network.dto.friend.request.DeleteFriendRequest
import com.turnin.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.turnin.core.data.source.network.dto.friend.response.FriendResponse
import com.turnin.core.data.source.network.dto.friend.response.FriendsResponse
import com.turnin.core.data.source.network.dto.friend.response.IncomingRequestsResponse
import com.turnin.core.data.source.network.util.NetworkResult

interface FriendNetworkDataSource {
    /**
     * 친구 목록 조회 (페이지네이션)
     *
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     *
     * @return [FriendsResponse]
     */
    suspend fun getFriends(
        userId: Long,
        page: Long,
        size: Int,
    ): NetworkResult<FriendsResponse>

    /**
     * 나에게 들어온 친구 요청 목록 조회 (페이지네이션)
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    suspend fun getIncomingRequests(
        page: Long,
        size: Int,
    ): NetworkResult<IncomingRequestsResponse>

    /**
     * 친구 추가
     *
     * @param addFriendRequest 친구 추가 요청 바디
     * @return [FriendResponse] 친구 응답 바디
     * @see FriendApi.addFriend
     */
    suspend fun addFriend(
        addFriendRequest: AddFriendRequest,
    ): NetworkResult<Unit>

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
     * 친구 상태 수정
     *
     * @param patchFriendStatusRequest 친구 상태 수정 요청 바디
     * @see FriendApi.updateFriendStatus
     */
    suspend fun updateFriendStatus(
        patchFriendStatusRequest: PatchFriendStatusRequest,
    ): NetworkResult<Unit>
}
