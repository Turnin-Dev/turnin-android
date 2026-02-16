package com.peekr.domain.block.usecase

import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.BlockId
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
     */
    operator fun invoke(blockId: Long): Flow<Result<Unit, BlockErrorType>> = flow {
        val blockIdVO = BlockId(blockId)
        emitAll(
            blockRepository.deleteBlock(blockIdVO)
                .mapError { commonError ->
                    BlockErrorType.CommonError(commonError)
                },
        )
    }
}
