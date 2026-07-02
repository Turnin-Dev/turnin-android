package com.turnin.domain.block.usecase

import com.turnin.core.domain.block.model.BlockReasonId
import com.turnin.core.domain.block.model.CreateBlock
import com.turnin.core.domain.block.repository.BlockRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.block.error.BlockErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 차단 생성
 *
 * @see invoke
 */
class CreateBlockUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val blockRepository: BlockRepository,
) {
    /**
     * 차단을 생성한다.
     *
     * @param blockedId 차단할 사용자 ID
     * @param reasonId 차단 사유 ID
     * @param customReason 기타 차단 사유
     */
    operator fun invoke(
        blockedId: Long?,
        reasonId: Long?,
        customReason: String?,
    ): Flow<Result<Unit, BlockErrorType>> = flow {
        val myUserId = userRepository.getMyUserId()

        // 1) 데이터 확인
        if (myUserId == null || blockedId == null || reasonId == null) {
            emit(Result.Error(BlockErrorType.MissingBlockTarget))
            return@flow
        }

        // 2) 차단 요청 모델 생성
        val createBlock = CreateBlock(
            blockerId = myUserId,
            blockedId = UserId(blockedId),
            reasonId = BlockReasonId(reasonId),
            customReason = customReason,
        )

        // 3) 차단 수행
        emitAll(
            blockRepository.createBlock(createBlock)
                .mapError { commonError ->
                    when (commonError) {
                        CommonErrorType.Network.Forbidden -> BlockErrorType.RequesterIdBlockerIdNotSame
                        else -> BlockErrorType.CommonError(commonError)
                    }
                },
        )
    }
}
