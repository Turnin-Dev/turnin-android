package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.AccountApi
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class AccountNetworkDataSourceImpl @Inject constructor(
    private val accountApi: AccountApi,
) : AccountNetworkDataSource {
    override suspend fun deleteAccount(): NetworkResult<Unit> =
        networkCallWithoutResponse { accountApi.deleteAccount() }
}
