package com.peekr.presentation.keywordDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import com.peekr.presentation.keywordDetail.view.KeywordDetailScreen
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute(
    onNavigateToReport: (userId: Long?, userKeywordId: Long?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: KeywordDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is KeywordDetailContract.UiEffect.NavigateToReport -> {
                onNavigateToReport(it.userId, it.userKeywordId)
            }
        }
    }

    KeywordDetailScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onBackPressed = onBackPressed,
    )
}
