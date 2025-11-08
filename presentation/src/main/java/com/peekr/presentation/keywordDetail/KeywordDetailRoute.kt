package com.peekr.presentation.keywordDetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.presentation.util.ObserveAsEvents
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import com.peekr.presentation.keywordDetail.view.KeywordDetailModal
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: KeywordDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isFullScreenError by rememberSaveable { mutableStateOf(false) }
    var fullScreenErrorMessage by rememberSaveable { mutableStateOf("") }

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is KeywordDetailContract.UiEffect.FullScreenError -> {
                fullScreenErrorMessage = it.errorMessage.asString(context)
                isFullScreenError = true
            }

            KeywordDetailContract.UiEffect.BackStack -> {
                onCancel()
            }
        }
    }

    KeywordDetailModal(
        modifier = modifier,
        sheetState = sheetState,
        myKeyword = uiState.myKeyword,
        keyword = uiState.keyword,
        description = uiState.description,
        editMode = uiState.editMode,
        loading = uiState.loading,
        loadingDescription = uiState.loadingDescription,
        fullScreenError = isFullScreenError,
        fullScreenErrorMessage = fullScreenErrorMessage,
        onUiEvent = viewModel::processEvent,
        onForceCancel = onCancel,
    )
}
