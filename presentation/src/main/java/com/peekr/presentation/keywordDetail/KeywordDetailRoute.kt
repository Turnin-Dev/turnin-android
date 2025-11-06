package com.peekr.presentation.keywordDetail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.keywordDetail.view.KeywordDetailModal
import com.peekr.presentation.keywordDetail.viewmodel.KeywordDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: KeywordDetailViewModel,
    onCancel: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KeywordDetailModal(
        modifier = modifier,
        sheetState = sheetState,
        myKeyword = true, // TODO: 추후 구현 예정
        keyword = uiState.keyword,
        description = uiState.description,
        onCancel = onCancel,
        onDismissRequest = onCancel,
    )
}
