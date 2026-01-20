package com.peekr.presentation.keywordDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.keywordDetail.view.KeywordDetailScreen
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute() {
    val viewModel: KeywordDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KeywordDetailScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUIEvent = viewModel::processEvent,
    )
}
