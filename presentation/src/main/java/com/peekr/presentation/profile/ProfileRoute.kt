package com.peekr.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.keyword.KeywordNameType
import com.peekr.core.presentation.keyword.UserKeywordIdType
import com.peekr.core.presentation.util.ObserveAsEvents
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.ProfileContract
import com.peekr.presentation.profile.view.AddKeywordModal
import com.peekr.presentation.profile.view.NodeOptionModal
import com.peekr.presentation.profile.view.ProfileScreen
import com.peekr.presentation.profile.view.SafeCancelModal
import com.peekr.presentation.profile.view.SafeDeleteModal
import com.peekr.presentation.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileRoute(
    onOpenKeywordDetailModal: (UserKeywordIdType, KeywordNameType) -> Unit,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isAddKeywordModalOpen by remember { mutableStateOf(false) }
    var isEditKeywordModalOpen by remember { mutableStateOf(false) }
    var isSafeCancelModalOpen by remember { mutableStateOf(false) }
    var isSafeDeleteModalOpen by remember { mutableStateOf(false) }
    var isNodeOptionModelOpen by remember { mutableStateOf(false) }

    var selectedUserKeywordId: UserKeywordId? by rememberSaveable { mutableStateOf(null) }
    var selectedKeyword: String? by rememberSaveable { mutableStateOf(null) }

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) {
        when (it) {
            ProfileContract.UiEffect.OpenSafeCancelModal -> {
                isSafeCancelModalOpen = true
            }

            ProfileContract.UiEffect.CloseAllModal -> {
                isSafeDeleteModalOpen = false
                isSafeCancelModalOpen = false
                isNodeOptionModelOpen = false
                isAddKeywordModalOpen = false
                isEditKeywordModalOpen = false
            }

            ProfileContract.UiEffect.ResetSelectedData -> {
                selectedKeyword = null
                selectedUserKeywordId = null
            }
        }
    }

    // ------------------------------ Modal ------------------------------
    AddKeywordModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isAddKeywordModalOpen,
        loading = uiState.loading,
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
            viewModel.processEvent(ProfileContract.UiEvent.AcceptSafeCancel)
        },
        onCancelClick = { isSafeCancelModalOpen = false },
    )

    SafeDeleteModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isSafeDeleteModalOpen,
        title = R.string.profile_screen_safe_modal_delete,
        onAcceptClick = {
            viewModel.processEvent(ProfileContract.UiEvent.DeleteKeyword(selectedUserKeywordId))
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
    ProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        profile = uiState.profile,
        loading = uiState.loading,
        onUiEvent = viewModel::processEvent,
        onOpenAddKeywordModal = { isAddKeywordModalOpen = true },
        onOpenNodeOptionModal = { userKeywordId, keyword ->
            selectedUserKeywordId = userKeywordId
            selectedKeyword = keyword
            isNodeOptionModelOpen = true
        },
        onOpenKeywordDetailModal = { userKeywordId, keyword ->
            onOpenKeywordDetailModal(userKeywordId, keyword)
        },
    )
}
