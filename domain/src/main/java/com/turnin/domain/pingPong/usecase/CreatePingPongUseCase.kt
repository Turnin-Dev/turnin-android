package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.common.error.catchUnexpected
import com.turnin.core.domain.common.error.mapError
import com.turnin.core.domain.common.validation.CommonValidationException
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPong
import com.turnin.core.domain.pingPong.model.PingPongQuestionContent
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * 핑퐁 질문 작성
 *
 * @see invoke
 */
class CreatePingPongUseCase @Inject constructor(
    private val pingPongRepository: PingPongRepository,
) {
    /**
     * 게시물에 질문을 작성한다.
     *
     * 질문 형식 규칙([PingPongQuestionContent.create])을 적용한 뒤 등록한다.
     *
     * @param userKeywordId 질문을 등록할 사용자 키워드(게시물) ID
     * @param question 사용자가 입력한 질문 내용
     */
    operator fun invoke(
        userKeywordId: Long,
        question: String,
    ): Flow<Result<PingPong, PingPongErrorType>> = flow {
        // 1) 질문 형식 규칙 적용
        val questionContent = try {
            PingPongQuestionContent.create(question)
        } catch (e: CommonValidationException) {
            emit(Result.Error(PingPongErrorType.InvalidQuestion, message = e.message))
            return@flow
        }

        // 2) 질문 등록
        emitAll(
            pingPongRepository.createPingPong(UserKeywordId(userKeywordId), questionContent)
                .mapError { error, code ->
                    when (code) {
                        ContractErrorCode.PingPong.PP001 -> PingPongErrorType.UserKeywordNotFound
                        ContractErrorCode.PingPong.PP002 -> PingPongErrorType.CannotQuestionOwnUserKeyword
                        else -> when (error) {
                            CommonErrorType.Network.TooManyRequests -> PingPongErrorType.TooManyQuestions
                            else -> PingPongErrorType.CommonError(error)
                        }
                    }
                },
        )
    }.catchUnexpected(PingPongErrorType::Unexpected)
}
