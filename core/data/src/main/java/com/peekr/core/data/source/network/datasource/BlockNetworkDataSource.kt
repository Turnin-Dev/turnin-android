package com.peekr.core.data.source.network.datasource

import com.peekr.core.data.source.network.dto.block.request.BlockRequest
import com.peekr.core.data.source.network.dto.block.response.BlockReasonResponse
import com.peekr.core.data.source.network.dto.block.response.BlockUsersResponse
import com.peekr.core.data.source.network.util.NetworkResult

/** Block 네트워크 데이터소스 */
interface BlockNetworkDataSource {
    /**
     * 차단 사용자 목록 조회 (페이지네이션)
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     *
     * @return [BlockUsersResponse]
     */
    suspend fun getBlockUsers(
        page: Long,
        size: Int,
    ): NetworkResult<BlockUsersResponse>

    /**
     * 차단 사유 목록 조회
     */
    suspend fun getBlockReasons(): NetworkResult<List<BlockReasonResponse>>

    /**
     * 차단 생성
     *
     * @param blockRequest 차단 요청 바디
     */
    suspend fun createBlock(
        blockRequest: BlockRequest,
    ): NetworkResult<Unit>

    /**
     * 차단 삭제
     *
     * @param blockId 차단 ID
     */
    suspend fun deleteBlock(
        blockId: Long,
    ): NetworkResult<Unit>
}
