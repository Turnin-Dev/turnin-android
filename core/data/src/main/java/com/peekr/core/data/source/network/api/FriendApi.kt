package com.peekr.core.data.source.network.api

import com.peekr.core.data.source.network.dto.friend.request.AddFriendRequest
import com.peekr.core.data.source.network.dto.friend.request.PatchFriendStatusRequest
import com.peekr.core.data.source.network.dto.friend.response.FriendResponse
import com.peekr.core.data.source.network.dto.friend.response.FriendsResponse
import com.peekr.core.data.source.network.dto.friend.response.IncomingRequestsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/** Friend Network API */
interface FriendApi {
    /**
     * 친구 목록 조회 (페이지네이션)
     */
    @GET(NetworkApiPath.Friend.LIST)
    suspend fun getFriends(
        @Query("userId") userId: Long,
        @Query("page") page: Long,
        @Query("size") size: Int,
    ): Response<FriendsResponse>

    /**
     * 나에게 들어온 친구 요청 목록 조회 (페이지네이션)
     */
    @GET(NetworkApiPath.Friend.INCOMING_REQUEST)
    suspend fun getIncomingRequests(
        @Query("page") page: Long,
        @Query("size") size: Int,
    ): Response<IncomingRequestsResponse>

    /**
     * 친구 추가
     *
     * HTTP 에러 상태코드 별 설명
     * - `403`: 요청자 ID와 실제 요청을 한 사용자 ID가 같지 않은 경우
     * - `404`: 사용자가 존재하지 않는 경우
     * - `409`: 이미 친구 요청을 했거나 친구 상태인 경우
     */
    @POST(NetworkApiPath.Friend.ROUTE)
    suspend fun addFriend(
        @Body addFriendRequest: AddFriendRequest,
    ): Response<FriendResponse>

    /**
     * 친구 삭제
     *
     * HTTP 에러 상태코드 별 설명
     * - `403`: 실제 요청을 한 사용자 ID가 요청자 ID, 요청 받을 ID와 모두 같지 않은 경우
     * - `404`: 친구 데이터에서 삭제 대상을 찾지 못하는 경우 (높은 확률로 이미 처리된 요청.)
     */
    @DELETE(NetworkApiPath.Friend.ROUTE)
    suspend fun deleteFriend(
        @Query("requesterId") requesterId: Long,
        @Query("receiverId") receiverId: Long,
    ): Response<Unit>

    /**
     * 친구 상태 수정
     *
     * HTTP 에러 상태코드 별 설명
     * - `403`: 요청자 ID와 실제 요청을 한 사용자 ID가 같지 않은 경우
     * - `404`: 친구 데이터에서 수정 대상을 찾지 못하는 경우 (높은 확률로 이미 처리된 요청.)
     */
    @PATCH(NetworkApiPath.Friend.STATUS)
    suspend fun updateFriendStatus(
        @Body patchFriendStatusRequest: PatchFriendStatusRequest,
    ): Response<Unit>
}
