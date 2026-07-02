package com.turnin.core.data.eventBus

import com.turnin.core.domain.eventBus.AuthEventBus
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class AuthEventBusImpl @Inject constructor() : AuthEventBus {
    private val _logoutEvent = Channel<Unit>(Channel.CONFLATED)
    override val logoutEvent: Flow<Unit> = _logoutEvent.receiveAsFlow()

    private val _loginEvent = Channel<Unit>(Channel.CONFLATED)
    override val loginEvent: Flow<Unit> = _loginEvent.receiveAsFlow()

    override fun emitLogout() {
        _logoutEvent.trySend(Unit)
    }

    override fun emitLogin() {
        _loginEvent.trySend(Unit)
    }
}
