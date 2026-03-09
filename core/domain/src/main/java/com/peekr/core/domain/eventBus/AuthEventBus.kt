package com.peekr.core.domain.eventBus

import kotlinx.coroutines.flow.SharedFlow

interface AuthEventBus {
    val logoutEvent: SharedFlow<Unit>

    suspend fun emitLogout()
}
