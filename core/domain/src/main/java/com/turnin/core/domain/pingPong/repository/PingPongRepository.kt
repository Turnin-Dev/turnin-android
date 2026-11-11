package com.turnin.core.domain.pingPong.repository

import androidx.paging.PagingData
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPong
import com.turnin.core.domain.pingPong.model.PingPongAnswer
import com.turnin.core.domain.pingPong.model.PingPongAnswerContent
import com.turnin.core.domain.pingPong.model.PingPongDetail
import com.turnin.core.domain.pingPong.model.PingPongQuestionContent
import kotlinx.coroutines.flow.Flow

/** 핑퐁 리포지토리 */
interface PingPongRepository {
    /**
     * 게시물에 달린 핑퐁(질문 + 답변) 목록 조회 (최신순, 커서 기반 페이지네이션)
     *
     * @param userKeywordId 조회할 사용자 키워드(게시물) ID
     */
    fun getPingPongs(userKeywordId: UserKeywordId): Flow<PagingData<PingPongDetail>>

    /**
     * 질문 작성
     *
     * @param userKeywordId 질문을 등록할 사용자 키워드(게시물) ID
     * @param question 질문 형식 규칙이 적용된 질문 내용 ([PingPongQuestionContent.create])
     */
    fun createPingPong(
        userKeywordId: UserKeywordId,
        question: PingPongQuestionContent,
    ): Flow<Result<PingPong, CommonErrorType>>

    /**
     * 답변 작성
     *
     * @param pingPongId 답변을 등록할 핑퐁 ID
     * @param answer 답변 내용
     */
    fun createAnswer(
        pingPongId: PingPongId,
        answer: PingPongAnswerContent,
    ): Flow<Result<PingPongAnswer, CommonErrorType>>

    /**
     * 질문 삭제 (연결된 답변도 함께 삭제된다.)
     *
     * @param pingPongId 삭제할 핑퐁 ID
     */
    fun deletePingPong(pingPongId: PingPongId): Flow<Result<Unit, CommonErrorType>>

    /**
     * 답변 삭제 (질문은 유지되며 다시 답변할 수 있다.)
     *
     * @param pingPongId 삭제할 답변이 달린 핑퐁 ID
     */
    fun deletePingPongAnswer(pingPongId: PingPongId): Flow<Result<Unit, CommonErrorType>>
}
