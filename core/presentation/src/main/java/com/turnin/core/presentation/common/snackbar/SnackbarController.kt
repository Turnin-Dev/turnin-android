package com.turnin.core.presentation.common.snackbar

import com.turnin.core.presentation.ui.util.UiText
import kotlinx.coroutines.flow.Flow

data class SnackbarEvent(
    val message: UiText,
    val action: SnackbarAction? = null,
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit,
)

/**
 * Turnin Snackbar Controller
 *
 * ### 사용 예시
 *     fun showSnackbar() {
 *         viewModelScope.launch {
 *             snackbarController.sendEvent(
 *                 event = SnackbarEvent(
 *                     message = "Event Message",
 *                     action = SnackbarAction(
 *                         name = "Click me!",
 *                         action = {
 *                             snackbarController.sendEvent(
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
interface SnackbarController {
    val events: Flow<SnackbarEvent>

    suspend fun sendEvent(event: SnackbarEvent)
}
