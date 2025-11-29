package com.peekr.presentation.profile

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
import com.peekr.core.presentation.feature.keyword.KeywordNameType
import com.peekr.core.presentation.feature.keyword.UserIdType
import com.peekr.core.presentation.feature.keyword.UserKeywordIdType
import com.peekr.core.presentation.ui.util.LockScreenOrientation
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.ProfileContract
import com.peekr.presentation.profile.view.MyProfileScreen
import com.peekr.presentation.profile.view.modal.AddKeywordModal
import com.peekr.presentation.profile.view.modal.NodeOptionModal
import com.peekr.presentation.profile.view.modal.SafeCancelModal
import com.peekr.presentation.profile.view.modal.SafeDeleteModal
import com.peekr.presentation.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyProfileRoute(
    onOpenKeywordDetailModal: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
    onSettingClick: () -> Unit,
) {
    // Lock Orientation
    LockScreenOrientation()

    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isAddKeywordModalOpen by rememberSaveable { mutableStateOf(false) }
    var isEditKeywordModalOpen by rememberSaveable { mutableStateOf(false) }
    var isSafeCancelModalOpen by rememberSaveable { mutableStateOf(false) }
    var isSafeDeleteModalOpen by rememberSaveable { mutableStateOf(false) }
    var isNodeOptionModelOpen by rememberSaveable { mutableStateOf(false) }

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            ProfileContract.UiEffect.OpenSafeCancelModal -> {
                isSafeCancelModalOpen = true
            }

            ProfileContract.UiEffect.CloseAllModals -> {
                isSafeDeleteModalOpen = false
                isSafeCancelModalOpen = false
                isNodeOptionModelOpen = false
                isAddKeywordModalOpen = false
                isEditKeywordModalOpen = false
            }
        }
    }

    // ------------------------------ Modal ------------------------------
    AddKeywordModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isAddKeywordModalOpen,
        loading = uiState.fullScreenLoading,
        keywordTextFieldState = uiState.keywordTextField,
        onKeywordTextChanged = {
            viewModel.processEvent(ProfileContract.UiEvent.OnKeywordTextChanged(it))
        },
        keywordDescTextFieldState = uiState.keywordDescTextField,
        onKeywordDescTextChanged = {
            viewModel.processEvent(ProfileContract.UiEvent.OnKeywordDescTextChanged(it))
        },
        onAddClick = {
            viewModel.processEvent(
                ProfileContract.UiEvent.AddKeyword(
                    uiState.keywordTextField.value,
                    uiState.keywordDescTextField.value,
                ),
            )
        },
        onCancelClick = {
            viewModel.processEvent(
                ProfileContract.UiEvent.CheckSafeCancel(
                    keyword = uiState.keywordTextField.value,
                    description = uiState.keywordDescTextField.value,
                ),
            )
        },
    )

    SafeCancelModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isSafeCancelModalOpen,
        title = R.string.profile_screen_safe_modal_cancel,
        onAcceptClick = {
            viewModel.processEvent(ProfileContract.UiEvent.CloseAllModals)
        },
        onCancelClick = { isSafeCancelModalOpen = false },
    )

    SafeDeleteModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isSafeDeleteModalOpen,
        title = R.string.profile_screen_safe_modal_delete,
        onAcceptClick = {
            viewModel.processEvent(
                ProfileContract.UiEvent.DeleteKeyword(uiState.selectedKeyword.userKeywordId),
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
        profile = uiState.profile,
        fullScreenLoading = uiState.fullScreenLoading,
        error = uiState.error,
        onUiEvent = viewModel::processEvent,
        onOpenAddKeywordModal = { isAddKeywordModalOpen = true },
        onOpenNodeOptionModal = { userKeywordId, keyword ->
            viewModel.processEvent(
                ProfileContract.UiEvent.OnSelectedKeywordChanged(userKeywordId, keyword),
            )
            isNodeOptionModelOpen = true
        },
        onOpenKeywordDetailModal = { userKeywordId, userId, keyword ->
            onOpenKeywordDetailModal(userKeywordId, userId, keyword)
        },
        onSettingClick = onSettingClick,
    )
}
