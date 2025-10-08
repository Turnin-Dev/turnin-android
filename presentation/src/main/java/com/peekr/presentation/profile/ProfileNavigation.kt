package com.peekr.presentation.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.presentation.profile.view.ProfileScreen
import com.peekr.presentation.profile.viewmodel.ProfileViewModel

fun NavGraphBuilder.profileNavigation() {
    composable<SubGraph.Profile> {
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()

        ProfileScreen(
            modifier = Modifier.fillMaxSize(),
            profile = profileState.profile,
        )
    }
}
