package com.peekr.presentation.keywordEdit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.R
import com.peekr.presentation.keywordEdit.state.KeywordEditContract
import com.peekr.presentation.keywordEdit.view.KeywordEditScreen
import com.peekr.presentation.keywordEdit.view.modal.SafeCancelModal
import com.peekr.presentation.keywordEdit.viewmodel.KeywordEditViewModel

@Composable
internal fun KeywordEditRoute(
    onBackPressed: () -> Unit,
) {
    val viewModel: KeywordEditViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isOpenSafeCancelModal by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        viewModel.processEvent(KeywordEditContract.UiEvent.SafeBackPressed)
    }

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            KeywordEditContract.UiEffect.CloseScreen -> {
                onBackPressed()
            }

            KeywordEditContract.UiEffect.OpenSafeCancelModal -> {
                isOpenSafeCancelModal = true
            }
        }
    }

    // ------------------------------ Modal ------------------------------
    SafeCancelModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isOpenSafeCancelModal,
        title = R.string.keyword_edit_modal_safe_cancel,
        onAcceptClick = {
            viewModel.processEvent(KeywordEditContract.UiEvent.CloseScreen)
            isOpenSafeCancelModal = false
        },
        onCancelClick = { isOpenSafeCancelModal = false },
    )

    // ------------------------------ Screen ------------------------------
    KeywordEditScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
    )
}
