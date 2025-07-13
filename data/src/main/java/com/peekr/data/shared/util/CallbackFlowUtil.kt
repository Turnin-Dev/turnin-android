package com.peekr.data.shared.util

import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.SendChannel

fun <E> SendChannel<E>.trySendAndClose(
    element: E,
    channelResult: ((ChannelResult<Unit>) -> Unit)? = null,
) {
    val result = trySend(element)
    channelResult?.let {
        it(result)
    }
    close()
}
