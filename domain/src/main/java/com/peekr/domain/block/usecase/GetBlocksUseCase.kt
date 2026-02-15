package com.peekr.domain.block.usecase

import androidx.paging.PagingData
import com.peekr.core.domain.block.model.Block
import com.peekr.core.domain.block.repository.BlockRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 차단 목록 조회 (페이지네이션)
 *
 * @see invoke
 */
class GetBlocksUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    /**
     * 차단 목록을 조회한다. (페이지네이션)
     */
    operator fun invoke(): Flow<PagingData<Block>> =
        blockRepository.getBlocks()
}
