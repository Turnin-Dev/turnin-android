package com.turnin.core.domain.block.repository

import androidx.paging.PagingData
import com.turnin.core.domain.block.model.BlockReason
import com.turnin.core.domain.block.model.BlockedUser
import com.turnin.core.domain.block.model.CreateBlock
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.BlockId
import com.turnin.core.domain.model.UserId
import kotlinx.coroutines.flow.Flow

/** 차단 리포지토리 */
interface BlockRepository {
    /**
     * 차단 사용자 목록 조회 (페이지네이션)
     */
    fun getBlockedUsers(): Flow<PagingData<BlockedUser>>

    /**
     * 차단 사유 목록 조회
     */
    fun getBlockReasons(): Flow<Result<List<BlockReason>, CommonErrorType>>

    /**
     * 차단 생성
     *
     * 잘못된 데이터가 노출될 수 있으므로 캐시 무효화는 코루틴 취소에 영향받지 않도록
     * [kotlinx.coroutines.NonCancellable] 컨텍스트에서 수행한다.
     *
     * @param block 차단 생성 요청 모델
     */
    fun createBlock(block: CreateBlock): Flow<Result<Unit, CommonErrorType>>

    /**
     * 차단 삭제
     *
     * @param blockId 차단 ID
     * @param userId 차단 삭제할 사용자 ID (캐시 무효화에 사용된다.)
     */
    fun deleteBlock(
        blockId: BlockId,
        userId: UserId,
    ): Flow<Result<Unit, CommonErrorType>>
}
