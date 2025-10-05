package com.peekr.core.data.user.network

import com.peekr.core.data.network.NetworkApiPath
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserProfileResponse
import com.peekr.core.data.user.network.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

/** User Network API */
interface UserApi {
    /** 사용자 조회 */
    @GET(NetworkApiPath.User.ROUTE)
    suspend fun getUser(): Response<UserResponse>

    /** 사용자 프로필 조회 */
    @GET(NetworkApiPath.User.PROFILE)
    suspend fun getUserProfile(): Response<UserProfileResponse>

    /** 사용자 수정 */
    @PATCH(NetworkApiPath.User.ROUTE)
    suspend fun updateUser(
        @Body userPatchRequest: UserPatchRequest,
    ): Response<Unit>
}
