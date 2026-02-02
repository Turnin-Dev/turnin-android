package com.peekr.presentation.profile.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.profile.view.MyProfileScreen
import com.peekr.presentation.profile.viewmodel.MyProfileViewModel

@Composable
fun MyProfileRoute(
    onSettingClick: () -> Unit,
    onFriendsCountClick: (Long) -> Unit,
    onNavigateToKeywordAddScreen: () -> Unit,
    onNavigateToKeywordDetail: (userId: Long, userKeywordId: Long) -> Unit,
) {
    val viewModel: MyProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyProfileScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        uiState = uiState,
        onUiEvent = viewModel::processEvent,
        onNavigateToKeywordAdd = onNavigateToKeywordAddScreen,
        onSettingClick = onSettingClick,
        onFriendsCountClick = onFriendsCountClick,
        onNavigateToKeywordDetail = onNavigateToKeywordDetail,
    )
}
