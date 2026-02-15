package com.peekr.domain.block.usecase

import com.peekr.core.domain.block.model.BlockReason
import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.domain.block.error.BlockErrorType
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
