package com.peekr.core.presentation.ui.component.snackbar

import androidx.annotation.VisibleForTesting
import com.peekr.core.presentation.ui.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val message: UiText,
    val action: SnackbarAction? = null,
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit,
)

/**
 * Peekr Snackbar Controller
 *
 * ### 사용 예시
 *     fun showSnackbar() {
 *         viewModelScope.launch {
 *             SnackbarController.sendEvent(
 *                 event = SnackbarEvent(
 *                     message = "Event Message",
 *                     action = SnackbarAction(
 *                         name = "Click me!",
 *                         action = {
 *                             SnackbarController.sendEvent(
 *                                 event = SnackbarEvent("Action Pressed!")
 *                             )
 *                         }
 *                     )
 *                 )
 *             )
 *         }
 *     }
 *
 *  @see SnackbarEvent
 *  @see SnackbarAction
 */
object SnackbarController {
    private var _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    /**
     * 채널로 이벤트 전송
     */
    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }

    /**
     * 테스트 격리를 위한 상태 초기화
     */
    @VisibleForTesting
    fun reset() {
        _events.close()
        _events = Channel()
    }
}
