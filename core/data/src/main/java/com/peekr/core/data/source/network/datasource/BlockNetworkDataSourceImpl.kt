package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.api.BlockApi
import com.peekr.core.data.source.network.dto.block.request.BlockRequest
import com.peekr.core.data.source.network.dto.block.response.BlockReasonResponse
import com.peekr.core.data.source.network.dto.block.response.BlocksResponse
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.data.source.network.util.networkCall
import com.peekr.core.data.source.network.util.networkCallWithoutResponse
import javax.inject.Inject

class BlockNetworkDataSourceImpl @Inject constructor(
    private val blockApi: BlockApi,
) : BlockNetworkDataSource {
    override suspend fun getBlocks(
        page: Long,
        size: Int,
    ): NetworkResult<BlocksResponse> =
        networkCall { blockApi.getBlocks(page, size) }

    override suspend fun getBlockReasons(): NetworkResult<List<BlockReasonResponse>> =
        networkCall { blockApi.getBlockReasons() }

    override suspend fun createBlock(blockRequest: BlockRequest): NetworkResult<Unit> =
        networkCallWithoutResponse { blockApi.createBlock(blockRequest) }

    override suspend fun deleteBlock(blockId: Long): NetworkResult<Unit> =
        networkCallWithoutResponse { blockApi.deleteBlock(blockId) }
}
