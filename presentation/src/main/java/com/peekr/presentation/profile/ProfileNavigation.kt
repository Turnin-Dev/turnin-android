package com.peekr.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.core.presentation.util.ObserveAsEvents
import com.peekr.presentation.R
import com.peekr.presentation.profile.state.ProfileContract
import com.peekr.presentation.profile.view.AddKeywordModal
import com.peekr.presentation.profile.view.CancelWarningModal
import com.peekr.presentation.profile.view.EditKeywordModal
import com.peekr.presentation.profile.view.NodeOptionModal
import com.peekr.presentation.profile.view.ProfileScreen
import com.peekr.presentation.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.profileNavigation() {
    composable<SubGraph.Profile> {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        var isAddKeywordModalOpen by remember { mutableStateOf(false) }
        var isEditKeywordModalOpen by remember { mutableStateOf(false) }
        var isCancelWarningModalOpen by remember { mutableStateOf(false) }
        var isNodeOptionModelOpen by remember { mutableStateOf(false) }
        var selectedUserKeywordId: UserKeywordId? by rememberSaveable { mutableStateOf(null) }
        var selectedKeyword: String? by rememberSaveable { mutableStateOf(null) }
        var selectedDescription: String? by rememberSaveable { mutableStateOf(null) }

        // ------------------------------ UiEffect ------------------------------
        ObserveAsEvents(viewModel.effect) {
            when (it) {
                ProfileContract.UiEffect.SuccessDeleteKeyword -> {
                    isNodeOptionModelOpen = false
                    selectedUserKeywordId = null
                }

                ProfileContract.UiEffect.SuccessUpdateKeywordDesc -> {
                    isEditKeywordModalOpen = false
                    selectedUserKeywordId = null
                    viewModel.processEvent(ProfileContract.UiEvent.OnKeywordTextChanged(""))
                    viewModel.processEvent(ProfileContract.UiEvent.OnKeywordDescTextChanged(""))
                }

                ProfileContract.UiEffect.SuccessAddKeyword -> {
                    isAddKeywordModalOpen = false
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
                viewModel.processEvent(ProfileContract.UiEvent.AddKeyword)
            },
            onCancelClick = {
                if (uiState.keywordTextField.value.isNotBlank() ||
                    uiState.keywordDescTextField.value.isNotBlank()
                ) {
                    isCancelWarningModalOpen = true
                } else {
                    isAddKeywordModalOpen = false
                }
            },
        )

        EditKeywordModal(
            modifier = Modifier.fillMaxSize(),
            isOpen = isEditKeywordModalOpen,
            loading = uiState.loading,
            keywordTextFieldReadOnly = true,
            keyword = selectedKeyword
                ?: stringResource(R.string.profile_error_not_selected_user_keyword_id),
            keywordDescTextFieldState = uiState.keywordDescTextField,
            onKeywordDescTextChanged = {
                viewModel.processEvent(ProfileContract.UiEvent.OnKeywordDescTextChanged(it))
            },
            onEditClick = {
                if (selectedDescription != uiState.keywordDescTextField.value) {
                    viewModel.processEvent(
                        ProfileContract.UiEvent.UpdateKeywordDescription(
                            userKeywordId = selectedUserKeywordId,
                            description = uiState.keywordDescTextField.value,
                        ),
                    )
                } else {
                    isEditKeywordModalOpen = false
                }
            },
            onCancelClick = {
                if (uiState.keywordTextField.value.isNotBlank() ||
                    uiState.keywordDescTextField.value.isNotBlank()
                ) {
                    isCancelWarningModalOpen = true
                } else {
                    isEditKeywordModalOpen = false
                }
            },
        )

        CancelWarningModal(
            modifier = Modifier.fillMaxSize(),
            isOpen = isCancelWarningModalOpen,
            onDeleteClick = {
                viewModel.processEvent(ProfileContract.UiEvent.OnKeywordTextChanged(""))
                viewModel.processEvent(ProfileContract.UiEvent.OnKeywordDescTextChanged(""))
                isCancelWarningModalOpen = false
                isAddKeywordModalOpen = false
                isEditKeywordModalOpen = false
            },
            onCancel = { isCancelWarningModalOpen = false },
        )

        if (isNodeOptionModelOpen) {
            NodeOptionModal(
                sheetState = sheetState,
                onDismissRequest = { isNodeOptionModelOpen = false },
                onEdit = {
                    viewModel.processEvent(
                        ProfileContract.UiEvent.OnKeywordDescTextChanged(selectedDescription ?: ""),
                    )
                    isEditKeywordModalOpen = true
                    isNodeOptionModelOpen = false
                },
                onDelete = {
                    viewModel.processEvent(ProfileContract.UiEvent.DeleteKeyword(selectedUserKeywordId))
                },
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
            onOpenNodeOptionModal = { userKeywordId, keyword, description ->
                selectedUserKeywordId = userKeywordId
                selectedKeyword = keyword
                selectedDescription = description
                isNodeOptionModelOpen = true
            },
        )
    }
}
