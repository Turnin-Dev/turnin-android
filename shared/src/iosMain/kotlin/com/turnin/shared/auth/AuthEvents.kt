package com.turnin.shared.auth

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** 인증 이벤트 (토큰 갱신 실패 → 자동 로그아웃) */
internal class AuthEvents {
    private val _authFailures = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** 구독 중일 때만 전달된다. */
    val authFailures: Flow<Unit> = _authFailures.asSharedFlow()

    fun notifyAuthFailure() {
        _authFailures.tryEmit(Unit)
    }
}
