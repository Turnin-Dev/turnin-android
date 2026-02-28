package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.util.NetworkResult

/** Account 네트워크 데이터소스 */
interface AccountNetworkDataSource {
    /**
     * 계정 삭제
     */
    suspend fun deleteAccount(): NetworkResult<Unit>
}
