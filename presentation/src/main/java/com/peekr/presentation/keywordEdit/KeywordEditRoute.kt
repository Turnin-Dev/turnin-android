package com.peekr.presentation.keywordEdit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.keywordEdit.view.KeywordEditScreen
import com.peekr.presentation.keywordEdit.viewmodel.KeywordEditViewModel

@Composable
internal fun KeywordEditRoute(
    onBackPressed: () -> Unit,
) {
    val viewModel: KeywordEditViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KeywordEditScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onAddClick = {},
        onBackPressed = onBackPressed,
    )
}
