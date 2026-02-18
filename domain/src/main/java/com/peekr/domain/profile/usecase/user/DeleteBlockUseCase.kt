package com.peekr.domain.profile.usecase.user

import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.mapError
import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.BlockId.Companion.invoke
import com.peekr.domain.profile.error.ProfileErrorType
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
    operator fun invoke(blockId: Long): Flow<Result<Unit, ProfileErrorType>> = flow {
        val blockIdVO = BlockId(blockId)
        emitAll(
            blockRepository.deleteBlock(blockIdVO)
                .mapError { commonError ->
                    ProfileErrorType.CommonError(commonError)
                },
        )
    }
}
