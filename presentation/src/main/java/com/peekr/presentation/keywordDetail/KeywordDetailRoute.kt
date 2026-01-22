package com.peekr.presentation.keywordDetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import com.peekr.presentation.keywordDetail.view.KeywordDetailScreen
import com.peekr.presentation.keywordDetail.view.MyKeywordOptionModal
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute(
    onNavigateToReport: (userId: Long?, userKeywordId: Long?) -> Unit,
    onNavigateToKeywordEdit: (userKeywordId: Long?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: KeywordDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var myKeywordOptionModal by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is KeywordDetailContract.UiEffect.NavigateToReport -> {
                onNavigateToReport(it.userId, it.userKeywordId)
            }
        }
    }

    if (myKeywordOptionModal) {
        MyKeywordOptionModal(
            sheetState = sheetState,
            onDismissRequest = { myKeywordOptionModal = false },
            onEdit = {
                myKeywordOptionModal = false
                onNavigateToKeywordEdit(uiState.keywordDetail?.userKeywordId)
            },
            onDelete = {},
            onCancel = {
                coroutineScope.launch {
                    sheetState.hide()
                    myKeywordOptionModal = false
                }
            },
        )
    }

    KeywordDetailScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onMoreClick = { myKeywordOptionModal = true },
        onBackPressed = onBackPressed,
    )
}
