package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.model.PingPongAnswerId
import com.turnin.core.domain.model.PingPongId

/**
 * 답변 모델
 *
 * @property id 답변 ID
 * @property pingPongId 답변이 달린 핑퐁 ID
 * @property answer 답변 내용
 * @property createdAt 생성 일자
 * @property updatedAt 수정 일자
 */
data class PingPongAnswer(
    val id: PingPongAnswerId,
    val pingPongId: PingPongId,
    val answer: PingPongAnswerContent,
    val createdAt: Long,
    val updatedAt: Long,
)
