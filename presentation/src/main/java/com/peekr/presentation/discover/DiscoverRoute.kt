package com.peekr.presentation.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.discover.view.DiscoverScreen
import com.peekr.presentation.discover.viewmodel.DiscoverViewModel

@Composable
fun DiscoverRoute() {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
    )
}
