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
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.feature.image.SinglePhotoPicker
import com.peekr.core.presentation.ui.component.modal.PeekrSimpleModal
import com.peekr.presentation.R
import com.peekr.presentation.setting.state.AccountInfoContract
import com.peekr.presentation.setting.view.detail.AccountInfoScreen
import com.peekr.presentation.setting.view.detail.ProfileImageUpdateModal
import com.peekr.presentation.setting.viewmodel.AccountInfoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoRoute(
    viewModel: AccountInfoViewModel,
    onNavigateToCropProfileImage: (uri: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localProfileImage by viewModel.localProfileImage.collectAsStateWithLifecycle()
    var isProfileImageUpdateModalOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var photoPickerOpen by remember { mutableStateOf(false) }
    var isOpenSafeCancelModal by remember { mutableStateOf(false) }

    // ------------------------------ UiEffect ------------------------------
    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            AccountInfoContract.UiEffect.CloseScreen -> {
                onBackPressed()
            }

            AccountInfoContract.UiEffect.OpenSafeCancelModal -> {
                isOpenSafeCancelModal = true
            }
        }
    }

    // ------------------------------ LoadingScreen ------------------------------
    if (uiState.fullScreenLoading) {
        PeekrLoadingScreen()
    }

    // ------------------------------ SafeCancelModal ------------------------------
    PeekrSimpleModal(
        modifier = Modifier.fillMaxSize(),
        isOpen = isOpenSafeCancelModal,
        title = R.string.setting_detail_account_info_safe_cancel_title,
        onAcceptClick = {
            onBackPressed()
            isOpenSafeCancelModal = false
        },
        onCancelClick = {
            isOpenSafeCancelModal = false
        },
    )

    // ------------------------------ PhotoPicker & Modal ------------------------------
    SinglePhotoPicker(
        open = photoPickerOpen,
        onSelected = { selectedImage, uri ->
            if (selectedImage != null && uri != null) {
                onNavigateToCropProfileImage(uri.toString())
            }
        },
        onClose = { photoPickerOpen = false },
    )

    if (isProfileImageUpdateModalOpen) {
        ProfileImageUpdateModal(
            existsProfileImage = localProfileImage != null ||
                uiState.accountInfo?.profileImageUrl != null,
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
                viewModel.processEvent(AccountInfoContract.UiEvent.OnProfileImageDeleted)
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
        accountInfo = uiState.accountInfo,
        localProfileImage = localProfileImage,
        isAccountInfoEdited = uiState.isAccountInfoEdited,
        displayIdState = viewModel.displayIdState,
        isDisplayIdValid = viewModel.isDisplayIdState,
        nameState = viewModel.nameState,
        isNameValid = viewModel.isNameValid,
        introduceState = viewModel.introduceState,
        isIntroduceValid = viewModel.isIntroduceValid,
        onUiEvent = viewModel::processEvent,
        onProfileImageClick = { isProfileImageUpdateModalOpen = true },
    )
}
