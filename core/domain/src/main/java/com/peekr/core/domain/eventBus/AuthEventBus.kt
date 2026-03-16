package com.peekr.core.domain.eventBus

import kotlinx.coroutines.flow.Flow

interface AuthEventBus {
    val logoutEvent: Flow<Unit>

    val loginEvent: Flow<Unit>

    fun emitLogout()

    fun emitLogin()
}
