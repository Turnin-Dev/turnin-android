package com.peekr.core.data.source.network.api

import retrofit2.Response
import retrofit2.http.DELETE

/** Account Network API */
interface AccountApi {
    /**
     * 계정 삭제 (일부 데이터는 비활성화한다.)
     */
    @DELETE(NetworkApiPath.Account.ROUTE)
    suspend fun deleteAccount(): Response<Unit>
}
