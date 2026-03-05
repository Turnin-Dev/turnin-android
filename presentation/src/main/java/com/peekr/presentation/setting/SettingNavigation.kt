package com.peekr.presentation.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToCropProfileImage
import com.peekr.core.presentation.common.viewmodel.sharedViewModel
import com.peekr.core.presentation.feature.image.rememberImageBitmap
import com.peekr.core.presentation.feature.image.toJpegByteArray
import com.peekr.presentation.register.view.CropProfileImageScreen
import com.peekr.presentation.setting.route.AccountInfoRoute
import com.peekr.presentation.setting.state.SettingContract
import com.peekr.presentation.setting.view.SettingScreen
import com.peekr.presentation.setting.viewmodel.SettingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
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

            AccountInfoRoute(
                viewModel = viewModel,
                onNavigateToCropProfileImage = { uri ->
                    navController.navigateToCropProfileImage(uri)
                },
                onBackPressed = {
                    navController.popBackStack()
                },
            )
        }

        composable<SubGraph.Setting.CropProfileImage> { backStackEntry ->
            val viewModel: SettingViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val scope = rememberCoroutineScope()
            val uri = backStackEntry.arguments?.getString("uri")?.toUri()
            val imageBitmap = rememberImageBitmap(uri)
            var screenLoading by rememberSaveable { mutableStateOf(false) }

            // ------------------------------ 안전 장치 ------------------------------
            BackHandler(screenLoading) { }

            LaunchedEffect(uri) {
                if (uri == null) {
                    navController.popBackStack()
                }
            }

            if (screenLoading) {
                PeekrLoadingScreen()
            }

            CropProfileImageScreen(
                modifier = Modifier.fillMaxSize(),
                image = imageBitmap,
                onCrop = { croppedImage ->
                    scope.launch {
                        screenLoading = true
                        try {
                            val bytes = withContext(Dispatchers.IO) {
                                croppedImage.toJpegByteArray()
                            }
                            viewModel.processEvent(SettingContract.UiEvent.OnProfileImageUpdated(bytes))
                            navController.popBackStack()
                        } finally {
                            screenLoading = false
                        }
                    }
                },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
