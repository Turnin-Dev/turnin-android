package com.peekr.core.presentation

import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSnackbarController : SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    override val events: Flow<SnackbarEvent>
        get() = _events.receiveAsFlow()

    override suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}
