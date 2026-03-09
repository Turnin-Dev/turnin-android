package com.peekr.core.data.eventBus

import com.peekr.core.domain.eventBus.AuthEventBus
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthEventBusImpl @Inject constructor() : AuthEventBus {
    private val _logoutEvent = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    override suspend fun emitLogout() {
        _logoutEvent.emit(Unit)
    }
}
