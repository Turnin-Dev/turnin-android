package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.block.request.BlockRequest
import com.turnin.core.data.source.network.dto.block.response.BlockReasonResponse
import com.turnin.core.data.source.network.dto.block.response.BlockedUserCursorPageResponse
import com.turnin.core.data.source.network.util.NetworkResult

/** Block 네트워크 데이터소스 */
interface BlockNetworkDataSource {
    /**
     * 차단 사용자 목록 조회 (페이지네이션)
     *
     * @param cursor 커서 값 (차단 ID)
     * @param size 페이지 크기
     *
     * @return [BlockedUserCursorPageResponse]
     */
    suspend fun getBlockedUsers(
        cursor: Long?,
        size: Int,
    ): NetworkResult<BlockedUserCursorPageResponse>

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
