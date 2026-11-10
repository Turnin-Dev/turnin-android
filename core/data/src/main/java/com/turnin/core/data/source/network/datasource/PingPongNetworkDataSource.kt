package com.turnin.core.data.source.network.datasource

import com.turnin.core.data.source.network.dto.pingPong.response.PingPongAnswerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongCursorPageResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongResponse
import com.turnin.core.data.source.network.util.NetworkResult

/**
 * 핑퐁 네트워크 데이터소스
 */
interface PingPongNetworkDataSource {
    /**
     * 핑퐁 질문/답변 목록 조회
     *
     * @param userKeywordId 사용자 키워드(게시물) ID
     * @param cursor 커서 값 (초기 호출 시 `null`)
     * @param size 페이지 크기 (1 ~ 25)
     */
    suspend fun getPingPongs(
        userKeywordId: Long,
        cursor: Long?,
        size: Int,
    ): NetworkResult<PingPongCursorPageResponse>

    /**
     * 핑퐁 생성
     *
     * @param userKeywordId 사용자 키워드(게시물) ID
     * @param question 질문 내용
     */
    suspend fun createPingPong(
        userKeywordId: Long,
        question: String,
    ): NetworkResult<PingPongResponse>

    /**
     * 핑퐁 답변 생성
     *
     * @param pingPongId 핑퐁 ID
     * @param answer 답변 내용
     */
    suspend fun createAnswer(
        pingPongId: Long,
        answer: String,
    ): NetworkResult<PingPongAnswerResponse>

    /**
     * 핑퐁 삭제
     *
     * @param pingPongId 핑퐁 ID
     */
    suspend fun deletePingPong(
        pingPongId: Long,
    ): NetworkResult<Unit>

    /**
     * 핑퐁 답변 삭제
     *
     * @param pingPongId 핑퐁 ID
     */
    suspend fun deletePingPongAnswer(
        pingPongId: Long,
    ): NetworkResult<Unit>
}
