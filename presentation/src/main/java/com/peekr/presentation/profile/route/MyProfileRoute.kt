package com.peekr.presentation.profile.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.MyProfileContract
import com.peekr.presentation.profile.view.MyProfileScreen
import com.peekr.presentation.profile.view.modal.NodeOptionModal
import com.peekr.presentation.profile.view.modal.SafeDeleteModal
import com.peekr.presentation.profile.viewmodel.MyProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyProfileRoute(
    onSettingClick: () -> Unit,
    onFriendsCountClick: (Long) -> Unit,
    onNavigateToKeywordAddScreen: () -> Unit,
) {
    val viewModel: MyProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isSafeCancelModalOpen by rememberSaveable { mutableStateOf(false) }
    var isSafeDeleteModalOpen by rememberSaveable { mutableStateOf(false) }
    var isNodeOptionModelOpen by rememberSaveable { mutableStateOf(false) }

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            MyProfileContract.UiEffect.OpenSafeCancelModal -> {
                isSafeCancelModalOpen = true
            }

            MyProfileContract.UiEffect.CloseAllModals -> {
                isSafeDeleteModalOpen = false
                isSafeCancelModalOpen = false
                isNodeOptionModelOpen = false
            }
        }
    }

    // ------------------------------ Modal ------------------------------
    SafeDeleteModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isSafeDeleteModalOpen,
        title = R.string.my_profile_modal_safe_delete,
        onAcceptClick = {
            viewModel.processEvent(
                MyProfileContract.UiEvent.DeleteKeyword(uiState.selectedKeyword.userKeywordId),
            )
        },
        onCancelClick = { isSafeDeleteModalOpen = false },
    )

    if (isNodeOptionModelOpen) {
        NodeOptionModal(
            sheetState = sheetState,
            onDismissRequest = { isNodeOptionModelOpen = false },
            onDelete = { isSafeDeleteModalOpen = true },
            onCancel = { isNodeOptionModelOpen = false },
        )
    }

    // ------------------------------ Screen ------------------------------
    MyProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        myProfile = uiState.myProfile,
        fullScreenLoading = uiState.fullScreenLoading,
        error = uiState.error,
        onUiEvent = viewModel::processEvent,
        onNavigateToKeywordAddScreen = onNavigateToKeywordAddScreen,
        onSettingClick = onSettingClick,
        onFriendsCountClick = onFriendsCountClick,
    )
}
