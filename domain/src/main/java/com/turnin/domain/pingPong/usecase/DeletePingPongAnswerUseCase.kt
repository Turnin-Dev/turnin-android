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
 * 핑퐁 답변 삭제
 *
 * @see invoke
 */
class DeletePingPongAnswerUseCase @Inject constructor(
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 답변을 삭제한다. 질문은 유지되며 다시 답변할 수 있다. (게시물 작성자만 가능)
     *
     * @param pingPongId 삭제할 답변이 달린 핑퐁 ID
     */
    operator fun invoke(pingPongId: Long): Flow<Result<Unit, PingPongErrorType>> =
        pingPongRepository.deletePingPongAnswer(PingPongId(pingPongId))
            .mapError { error, code ->
                when (code) {
                    ContractErrorCode.PingPong.PP003 -> PingPongErrorType.QuestionAlreadyDeleted
                    ContractErrorCode.PingPong.PP007 -> PingPongErrorType.NoPermissionToDeleteAnswer
                    ContractErrorCode.PingPong.PP008 -> PingPongErrorType.AnswerAlreadyDeleted
                    else -> PingPongErrorType.CommonError(error)
                }
            }
            .catchUnexpected(PingPongErrorType::Unexpected)
}
