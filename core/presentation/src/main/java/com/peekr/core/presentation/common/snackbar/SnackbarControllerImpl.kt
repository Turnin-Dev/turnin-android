package com.peekr.core.presentation.common.snackbar

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
class SnackbarControllerImpl @Inject constructor() : SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    override val events: Flow<SnackbarEvent>
        get() = _events.receiveAsFlow()

    override suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}
