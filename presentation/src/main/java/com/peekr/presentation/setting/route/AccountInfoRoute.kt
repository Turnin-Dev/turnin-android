package com.peekr.presentation.setting.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.feature.image.SinglePhotoPicker
import com.peekr.presentation.setting.state.SettingContract
import com.peekr.presentation.setting.view.detail.AccountInfoScreen
import com.peekr.presentation.setting.view.detail.ProfileImageUpdateModal
import com.peekr.presentation.setting.viewmodel.SettingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoRoute(
    viewModel: SettingViewModel,
    onNavigateToCropProfileImage: (uri: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accountInfoState by viewModel.accountInfoState.collectAsStateWithLifecycle()
    val localProfileImage by viewModel.localProfileImage.collectAsStateWithLifecycle()
    var isProfileImageUpdateModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var photoPickerOpen by remember { mutableStateOf(false) }

    // ------------------------------ LoadingScreen ------------------------------
    if (uiState.fullScreenLoading) {
        PeekrLoadingScreen()
    }

    // ------------------------------ PhotoPicker & Modal ------------------------------
    SinglePhotoPicker(
        open = photoPickerOpen,
        onSelected = { selectedImage, uri ->
            if (selectedImage != null) {
                onNavigateToCropProfileImage(uri.toString())
            }
        },
        onClose = { photoPickerOpen = false },
    )

    if (isProfileImageUpdateModalOpen) {
        ProfileImageUpdateModal(
            existsProfileImage = localProfileImage != null ||
                accountInfoState.accountInfo?.profileImageUrl != null,
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    isProfileImageUpdateModalOpen = false
                }
            },
            onCancel = {
                scope.launch {
                    sheetState.hide()
                    isProfileImageUpdateModalOpen = false
                }
            },
            onImageChange = {
                photoPickerOpen = true
                isProfileImageUpdateModalOpen = false
            },
            onImageChangeToDefault = {
                viewModel.processEvent(SettingContract.UiEvent.OnProfileImageDeleted)
                isProfileImageUpdateModalOpen = false
            },
            onImageAdd = {
                photoPickerOpen = true
                isProfileImageUpdateModalOpen = false
            },
        )
    }

    // ------------------------------ Screen ------------------------------
    AccountInfoScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        accountInfo = accountInfoState.accountInfo,
        localProfileImage = localProfileImage,
        isAccountInfoEdited = accountInfoState.isAccountInfoEdited,
        displayIdState = viewModel.displayIdState,
        isDisplayIdValid = viewModel.isDisplayIdState,
        nameState = viewModel.nameState,
        isNameValid = viewModel.isNameValid,
        introduceState = viewModel.introduceState,
        isIntroduceValid = viewModel.isIntroduceValid,
        onUiEvent = viewModel::processEvent,
        onProfileImageClick = { isProfileImageUpdateModalOpen = true },
        onBackPressed = onBackPressed,
    )
}
