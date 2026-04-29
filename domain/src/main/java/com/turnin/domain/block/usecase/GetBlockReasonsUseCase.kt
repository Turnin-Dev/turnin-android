package com.turnin.domain.block.usecase

import com.turnin.core.domain.block.model.BlockReason
import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.mapError
import com.turnin.domain.block.error.BlockErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 차단 사유 목록 조회
 *
 * @see invoke
 */
class GetBlockReasonsUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    /**
     * 차단 사유 목록을 조회한다.
     */
    operator fun invoke(): Flow<Result<List<BlockReason>, BlockErrorType>> =
        blockRepository.getBlockReasons()
            .mapError { commonError ->
                BlockErrorType.CommonError(commonError)
            }
}
