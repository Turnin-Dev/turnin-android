package com.peekr.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.presentation.profile.state.ProfileContract
import com.peekr.presentation.profile.view.AddKeywordModal
import com.peekr.presentation.profile.view.CancelWarningModal
import com.peekr.presentation.profile.view.ProfileScreen
import com.peekr.presentation.profile.viewmodel.ProfileViewModel

fun NavGraphBuilder.profileNavigation() {
    composable<SubGraph.Profile> {
        val viewModel: ProfileViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        var isAddKeywordModalOpen by remember { mutableStateOf(false) }
        var isCancelWarningModalOpen by remember { mutableStateOf(false) }

        // ------------------------------ Modal ------------------------------
        AddKeywordModal(
            modifier = Modifier.fillMaxSize(),
            isOpen = isAddKeywordModalOpen,
            loading = uiState.loading,
            canAdd = uiState.keywordTextField.value.isNotBlank(),
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
            onCancel = {
                if (uiState.keywordTextField.value.isNotBlank() ||
                    uiState.keywordDescTextField.value.isNotBlank()
                ) {
                    isCancelWarningModalOpen = true
                } else {
                    isAddKeywordModalOpen = false
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
            },
            onCancel = { isCancelWarningModalOpen = false },
        )

        // ------------------------------ Screen ------------------------------
        ProfileScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            profile = uiState.profile,
            onOpenAddKeywordModal = { isAddKeywordModalOpen = true },
        )
    }
}
