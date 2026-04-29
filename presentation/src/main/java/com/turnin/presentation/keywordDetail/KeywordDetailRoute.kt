package com.turnin.presentation.keywordDetail

import androidx.compose.foundation.background
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
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.presentation.common.navigation.args.UserProfileArgs
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.R
import com.turnin.presentation.keywordDetail.state.KeywordDetailContract
import com.turnin.presentation.keywordDetail.view.KeywordDetailScreen
import com.turnin.presentation.keywordDetail.view.MyKeywordOptionModal
import com.turnin.presentation.keywordDetail.view.modal.SafeDeleteModal
import com.turnin.presentation.keywordDetail.viewmodel.KeywordDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeywordDetailRoute(
    onNavigateToReport: (userId: Long?, userKeywordId: Long?) -> Unit,
    onNavigateToKeywordEdit: (userKeywordId: Long?) -> Unit,
    onNavigateToUserProfile: (args: UserProfileArgs) -> Unit,
    onNavigateToMyProfile: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel: KeywordDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isOptionModalOpen by remember { mutableStateOf(false) }
    var isDeleteModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.effect) {
        when (it) {
            is KeywordDetailContract.UiEffect.NavigateToReport -> {
                onNavigateToReport(it.userId, it.userKeywordId)
            }

            KeywordDetailContract.UiEffect.CloseScreen -> {
                onBackPressed()
            }
        }
    }

    if (isOptionModalOpen) {
        MyKeywordOptionModal(
            sheetState = sheetState,
            onDismissRequest = { isOptionModalOpen = false },
            onEdit = {
                isOptionModalOpen = false
                onNavigateToKeywordEdit(uiState.keywordDetail?.userKeywordId)
            },
            onDelete = {
                isDeleteModalOpen = true
                isOptionModalOpen = false
            },
            onCancel = {
                coroutineScope.launch {
                    sheetState.hide()
                    isOptionModalOpen = false
                }
            },
        )
    }

    SafeDeleteModal(
        isOpen = isDeleteModalOpen,
        title = R.string.keyword_detail_safe_delete_modal_title,
        onAcceptClick = {
            viewModel.processEvent(KeywordDetailContract.UiEvent.OnDelete)
            isDeleteModalOpen = false
        },
        onCancelClick = {
            isDeleteModalOpen = false
        },
    )

    KeywordDetailScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onMoreClick = { isOptionModalOpen = true },
        onUserClick = { args ->
            if (uiState.myKeyword) {
                onNavigateToMyProfile()
            } else {
                onNavigateToUserProfile(args)
            }
        },
        onBackPressed = onBackPressed,
    )
}
