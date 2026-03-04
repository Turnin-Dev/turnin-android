package com.peekr.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.viewmodel.sharedViewModel
import com.peekr.presentation.setting.view.SettingScreen
import com.peekr.presentation.setting.view.detail.AccountInfoScreen
import com.peekr.presentation.setting.viewmodel.SettingViewModel

fun NavGraphBuilder.settingNavigation(
    navController: NavHostController,
) {
    navigation<SubGraph.Setting.Root>(startDestination = SubGraph.Setting.Main) {
        composable<SubGraph.Setting.Main> { backStackEntry ->
            val viewModel: SettingViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)

            SettingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                onNavigateToAccountInfo = { navController.navigate(SubGraph.Setting.AccountInfo) },
                onBackPressed = { navController.popBackStack() },
            )
        }

        composable<SubGraph.Setting.AccountInfo> { backStackEntry ->
            val viewModel: SettingViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AccountInfoScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                accountInfo = uiState.accountInfo,
                isAccountInfoEdited = uiState.isAccountInfoEdited,
                displayIdState = viewModel.displayIdState,
                isDisplayIdValid = viewModel.isDisplayIdState,
                nameState = viewModel.nameState,
                isNameValid = viewModel.isNameValid,
                introduceState = viewModel.introduceState,
                isIntroduceValid = viewModel.isIntroduceValid,
                onBackPressed = { navController.popBackStack() },
            )
        }
    }
}
