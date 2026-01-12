package com.peekr.presentation.profile.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.profile.view.MyProfileScreen
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

    MyProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        myProfile = uiState.myProfile,
        myKeywords = uiState.myKeywords,
        loading = uiState.loading,
        fullScreenLoading = uiState.fullScreenLoading,
        error = uiState.error,
        onUiEvent = viewModel::processEvent,
        onNavigateToKeywordAddScreen = onNavigateToKeywordAddScreen,
        onSettingClick = onSettingClick,
        onFriendsCountClick = onFriendsCountClick,
    )
}
