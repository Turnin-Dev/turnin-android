package com.peekr.core.data.eventBus

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class AuthEventBus @Inject constructor() {
    private val _logoutEvent = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    suspend fun emitLogout() {
        _logoutEvent.emit(Unit)
    }
}
