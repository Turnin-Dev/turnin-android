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
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.feature.image.SinglePhotoPicker
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
    val accountInfoState by viewModel.accountInfoState.collectAsStateWithLifecycle()
    var isProfileImageUpdateModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var photoPickerOpen by remember { mutableStateOf(false) }

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
            existsProfileImage = accountInfoState.accountInfo?.profileImageUrl != null,
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
            onImageChangeToDefault = { },
            onImageAdd = {
                photoPickerOpen = true
                isProfileImageUpdateModalOpen = false
            },
        )
    }

    AccountInfoScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        accountInfo = accountInfoState.accountInfo,
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

// TODO: 1. 프사 변경 시 임시 반영되어야 함.
// TODO: 2. 이미지 편집기에서 해상도, 크기 작은 이미지 선택 시 버그 수정
