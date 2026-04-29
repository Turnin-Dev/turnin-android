package com.turnin.core.data.source.network.api

import retrofit2.Response
import retrofit2.http.DELETE

/** Account Network API */
interface AccountApi {
    /**
     * 계정 삭제
     */
    @DELETE(NetworkApiPath.Account.ROUTE)
    suspend fun deleteAccount(): Response<Unit>
}
