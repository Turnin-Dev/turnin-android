package com.peekr.domain.block.usecase

import com.peekr.core.domain.block.model.CreateBlock
import com.peekr.core.domain.block.repository.BlockRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.common.error.mapError
import com.peekr.domain.block.error.BlockErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 차단 생성
 *
 * @see invoke
 */
class CreateBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    /**
     * 차단을 생성한다.
     *
     * @param createBlock 차단 생성 요청 모델
     */
    operator fun invoke(createBlock: CreateBlock): Flow<Result<Unit, BlockErrorType>> =
        blockRepository.createBlock(createBlock)
            .mapError { commonError ->
                when (commonError) {
                    CommonErrorType.Network.Forbidden -> BlockErrorType.RequesterIdBlockerIdNotSame
                    else -> BlockErrorType.CommonError(commonError)
                }
            }
}
