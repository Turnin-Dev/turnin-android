package com.turnin.domain.pingPong.usecase

import javax.inject.Inject

class PingPongUseCases @Inject constructor(
    /**
     * 핑퐁 질문/답변 목록 조회
     * @see GetPingPongsUseCase
     */
    val getPingPongs: GetPingPongsUseCase,
    /**
     * 핑퐁 질문 작성
     * @see CreatePingPongUseCase
     */
    val createPingPong: CreatePingPongUseCase,
    /**
     * 핑퐁 답변 작성
     * @see CreatePingPongAnswerUseCase
     */
    val createAnswer: CreatePingPongAnswerUseCase,
    /**
     * 핑퐁 질문 삭제
     * @see DeletePingPongUseCase
     */
    val deletePingPong: DeletePingPongUseCase,
    /**
     * 핑퐁 답변 삭제
     * @see DeletePingPongAnswerUseCase
     */
    val deleteAnswer: DeletePingPongAnswerUseCase,
)
