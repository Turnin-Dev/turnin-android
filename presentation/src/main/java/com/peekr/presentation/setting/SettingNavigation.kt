package com.peekr.presentation.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.navigateToBlockList
import com.peekr.core.presentation.common.navigation.navigateToCropProfileImage
import com.peekr.core.presentation.common.navigation.navigateToLogin
import com.peekr.core.presentation.common.navigation.navigateToNotificationSetting
import com.peekr.core.presentation.common.navigation.navigateToQna
import com.peekr.core.presentation.common.navigation.navigateToVersionInfo
import com.peekr.core.presentation.feature.image.SimpleImageCropper
import com.peekr.core.presentation.feature.image.rememberImageBitmap
import com.peekr.core.presentation.feature.image.toJpegByteArray
import com.peekr.presentation.setting.route.AccountInfoRoute
import com.peekr.presentation.setting.route.SettingRoute
import com.peekr.presentation.setting.state.AccountInfoContract
import com.peekr.presentation.setting.view.detail.NotificationSettingScreen
import com.peekr.presentation.setting.view.detail.QnaScreen
import com.peekr.presentation.setting.view.detail.VersionInfoScreen
import com.peekr.presentation.setting.viewmodel.AccountInfoViewModel
import com.peekr.presentation.setting.viewmodel.NotificationSettingViewModel
import com.peekr.presentation.setting.viewmodel.VersionInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.settingNavigation(
    appNavController: NavHostController,
) {
    navigation<SubGraph.Setting.Root>(startDestination = SubGraph.Setting.Main) {
        composable<SubGraph.Setting.Main> {
            SettingRoute(
                onNavigateToAccountInfo = { accountInfo ->
                    appNavController.navigate(
                        SubGraph.Setting.AccountInfo(
                            displayId = accountInfo?.displayId,
                            name = accountInfo?.name,
                            introduce = accountInfo?.introduce,
                            profileImageUrl = accountInfo?.profileImageUrl,
                        ),
                    )
                },
                onNavigateToBlockList = {
                    appNavController.navigateToBlockList()
                },
                onNavigateToLogin = {
                    appNavController.navigateToLogin()
                },
                onNavigateToVersionInfo = {
                    appNavController.navigateToVersionInfo()
                },
                onNavigateToQna = { qnaUrl ->
                    appNavController.navigateToQna(qnaUrl)
                },
                onNavigateToNotification = {
                    appNavController.navigateToNotificationSetting()
                },
                onBackPressed = {
                    appNavController.popBackStack()
                },
            )
        }

        composable<SubGraph.Setting.AccountInfo> {
            val viewModel: AccountInfoViewModel = hiltViewModel()

            BackHandler {
                viewModel.processEvent(AccountInfoContract.UiEvent.SafeBackPressed)
            }

            AccountInfoRoute(
                viewModel = viewModel,
                onNavigateToCropProfileImage = { uri ->
                    appNavController.navigateToCropProfileImage(uri)
                },
                onBackPressed = {
                    appNavController.popBackStack()
                },
            )
        }

        composable<SubGraph.Setting.CropProfileImage> { backStackEntry ->
            val accountInfoEntry = remember(backStackEntry) {
                appNavController.getBackStackEntry<SubGraph.Setting.AccountInfo>()
            }
            val viewModel: AccountInfoViewModel = hiltViewModel(accountInfoEntry)
            val scope = rememberCoroutineScope()
            val uri = backStackEntry.arguments?.getString("uri")?.toUri()
            val imageBitmap = rememberImageBitmap(uri)
            var screenLoading by rememberSaveable { mutableStateOf(false) }

            // ------------------------------ 안전 장치 ------------------------------
            BackHandler(screenLoading) { }

            LaunchedEffect(uri) {
                if (uri == null) {
                    appNavController.popBackStack()
                }
            }

            if (screenLoading) {
                PeekrLoadingScreen()
            }

            SimpleImageCropper(
                modifier = Modifier.fillMaxSize(),
                image = imageBitmap,
                onCrop = { croppedImage ->
                    scope.launch {
                        screenLoading = true
                        try {
                            val bytes = withContext(Dispatchers.IO) {
                                croppedImage.toJpegByteArray()
                            }
                            viewModel.processEvent(
                                AccountInfoContract.UiEvent.OnProfileImageUpdated(bytes),
                            )
                            appNavController.popBackStack()
                        } finally {
                            screenLoading = false
                        }
                    }
                },
                onCancel = { appNavController.popBackStack() },
            )
        }

        composable<SubGraph.Setting.VersionInfo> {
            val viewModel: VersionInfoViewModel = hiltViewModel()

            VersionInfoScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                versionName = viewModel.appVersion,
                onServiceTermClick = { },
                onPrivacyPolicyClick = { },
                onBackPressed = { appNavController.popBackStack() },
            )
        }

        composable<SubGraph.Setting.Qna> { backStackEntry ->
            val qnaUrl = backStackEntry.arguments?.getString("qnaUrl")

            LaunchedEffect(qnaUrl) {
                if (qnaUrl == null) {
                    appNavController.popBackStack()
                }
            }

            QnaScreen(
                modifier = Modifier.fillMaxSize(),
                formUrl = qnaUrl,
                onBackPressed = { appNavController.popBackStack() },
            )
        }

        composable<SubGraph.Setting.NotificationSetting> {
            val viewModel: NotificationSettingViewModel = hiltViewModel()
            val appSetting by viewModel.appSetting.collectAsStateWithLifecycle()

            NotificationSettingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                isPushEnabled = appSetting.pushNotificationEnabled,
                togglePush = viewModel::togglePushNotificationAndSync,
                onBackPressed = { appNavController.popBackStack() },
            )
        }
    }
}
