package com.turnin.core.common.coroutine

import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.SendChannel

/**
 * [SendChannel]의 확장함수로서, 주 목적은 [kotlinx.coroutines.flow.callbackFlow]에서
 * `trySend`와 `close`를 매 번 호출하는 번거로움을 줄이기 위해 사용한다.
 *
 * @param element [kotlinx.coroutines.flow.callbackFlow]의 `element`와 같은 역할을 한다.
 * @param channelResult `close`를 수행하기 전에 `trySend`의 결과 값을 기반으로 수행할 작업
 */
fun <E> SendChannel<E>.trySendAndClose(
    element: E,
    channelResult: ((ChannelResult<Unit>) -> Unit)? = null,
) {
    val result = trySend(element)
    channelResult?.invoke(result)
    close()
}
