package com.peekr.core.data.eventBus

import com.peekr.core.domain.eventBus.AuthEventBus
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class AuthEventBusImpl @Inject constructor() : AuthEventBus {
    private val _logoutEvent = Channel<Unit>(Channel.CONFLATED)
    override val logoutEvent: Flow<Unit> = _logoutEvent.receiveAsFlow()

    override fun emitLogout() {
        _logoutEvent.trySend(Unit)
    }
}
