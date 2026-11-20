package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.common.error.catchUnexpected
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.pingPong.model.PingPongAnswer
import com.turnin.core.domain.pingPong.model.PingPongAnswerContent
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 핑퐁 답변 작성
 *
 * @see invoke
 */
class CreatePingPongAnswerUseCase @Inject constructor(
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 질문에 답변을 작성한다. (게시물 작성자만 가능)
     *
     * @param pingPongId 답변을 등록할 핑퐁 ID
     * @param answer 사용자가 입력한 답변 내용
     */
    operator fun invoke(
        pingPongId: Long,
        answer: String,
    ): Flow<Result<PingPongAnswer, PingPongErrorType>> = flow {
        // 1) 답변 내용 검증
        val answerContent = try {
            PingPongAnswerContent(answer)
        } catch (e: CommonValidationException) {
            emit(Result.Error(PingPongErrorType.InvalidAnswer, message = e.message))
            return@flow
        }

        // 2) 답변 등록
        emitAll(
            pingPongRepository.createAnswer(PingPongId(pingPongId), answerContent)
                .mapError { error, code ->
                    when (code) {
                        ContractErrorCode.PingPong.PP001 -> PingPongErrorType.UserKeywordNotFound
                        ContractErrorCode.PingPong.PP003 -> PingPongErrorType.QuestionNotFound
                        ContractErrorCode.PingPong.PP004 -> PingPongErrorType.NotUserKeywordOwner
                        ContractErrorCode.PingPong.PP005 -> PingPongErrorType.AlreadyAnswered
                        ContractErrorCode.PingPong.PP006 -> PingPongErrorType.CannotAnswerBlockedQuestioner
                        else -> PingPongErrorType.CommonError(error)
                    }
                },
        )
    }.catchUnexpected(PingPongErrorType::Unexpected)
}
