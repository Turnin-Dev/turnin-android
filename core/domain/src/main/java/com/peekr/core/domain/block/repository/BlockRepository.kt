package com.peekr.core.domain.block.repository

import androidx.paging.PagingData
import com.peekr.core.domain.block.model.Block
import com.peekr.core.domain.block.model.BlockReason
import com.peekr.core.domain.block.model.CreateBlock
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.BlockId
import kotlinx.coroutines.flow.Flow

/** 차단 리포지토리 */
interface BlockRepository {
    /**
     * 차단 목록 조회 (페이지네이션)
     */
    fun getBlocks(): Flow<PagingData<Block>>

    /**
     * 차단 사유 목록 조회
     */
    fun getBlockReasons(): Flow<Result<List<BlockReason>, CommonErrorType>>

    /**
     * 차단 생성
     *
     * @param block 차단 생성 요청 모델
     */
    fun createBlock(block: CreateBlock): Flow<Result<Unit, CommonErrorType>>

    /**
     * 차단 삭제
     *
     * @param blockId 차단 ID
     */
    fun deleteBlock(blockId: BlockId): Flow<Result<Unit, CommonErrorType>>
}
