package com.turnin.presentation.profile.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.presentation.common.util.ObserveAsEvents
import com.turnin.presentation.profile.state.UserProfileContract
import com.turnin.presentation.profile.view.UserProfileScreen
import com.turnin.presentation.profile.view.modal.DeleteFriendModal
import com.turnin.presentation.profile.viewmodel.UserProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserProfileRoute(
    onBackPressed: () -> Unit,
    navigateToReport: (Long) -> Unit,
    navigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
    navigateToFriendsList: (userId: Long) -> Unit,
) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var openDeleteFriendModal by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is UserProfileContract.UiEffect.NavigateToReport -> {
                navigateToReport(effect.userId)
            }

            UserProfileContract.UiEffect.OpenDeleteFriendModal -> {
                openDeleteFriendModal = true
            }
        }
    }

    // ------------------------------ Modal ------------------------------
    if (openDeleteFriendModal) {
        DeleteFriendModal(
            sheetState = sheetState,
            onDismissRequest = { openDeleteFriendModal = false },
            onCancel = { openDeleteFriendModal = false },
            onDeleteFriend = {
                viewModel.processEvent(UserProfileContract.UiEvent.DeleteFriend)
                scope.launch {
                    sheetState.hide()
                    openDeleteFriendModal = false
                }
            },
        )
    }

    // ------------------------------ Screen ------------------------------
    UserProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onNavigateToKeywordDetail = { userId, userKeywordId ->
            navigateToKeywordDetail(userId, userKeywordId)
        },
        onNavigateToFriendsList = { userId ->
            navigateToFriendsList(userId)
        },
        onBackPressed = onBackPressed,
    )
}
