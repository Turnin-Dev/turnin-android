package com.peekr.domain.block.usecase

import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.UserId
import com.peekr.domain.block.error.BlockErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 차단 해제(삭제)
 *
 * @see invoke
 */
class DeleteBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    /**
     * 차단을 해제(삭제)한다.
     *
     * @param blockId 차단 ID
     * @param userId 차단 해제할 사용자 ID
     */
    operator fun invoke(
        blockId: Long,
        userId: Long,
    ): Flow<Result<Unit, BlockErrorType>> = flow {
        val blockIdVO = BlockId(blockId)
        val userIdVO = UserId(userId)
        emitAll(
            blockRepository.deleteBlock(blockIdVO, userIdVO)
                .mapError { commonError ->
                    BlockErrorType.CommonError(commonError)
                },
        )
    }
}
