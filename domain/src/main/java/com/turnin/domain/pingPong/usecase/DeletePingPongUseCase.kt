package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.common.error.catchUnexpected
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 핑퐁 질문 삭제
 *
 * @see invoke
 */
class DeletePingPongUseCase @Inject constructor(
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 질문을 삭제한다. 연결된 답변도 함께 삭제된다. (질문자 또는 게시물 작성자만 가능)
     *
     * @param pingPongId 삭제할 핑퐁 ID
     */
    operator fun invoke(pingPongId: Long): Flow<Result<Unit, PingPongErrorType>> =
        pingPongRepository.deletePingPong(PingPongId(pingPongId))
            .mapError { error, code ->
                when (code) {
                    ContractErrorCode.PingPong.PP003 -> PingPongErrorType.QuestionAlreadyDeleted
                    ContractErrorCode.PingPong.PP007 -> PingPongErrorType.NoPermissionToDeleteQuestion
                    else -> PingPongErrorType.CommonError(error)
                }
            }
            .catchUnexpected(PingPongErrorType::Unexpected)
}
