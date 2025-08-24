package com.peekr.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.presentation.R
import com.peekr.presentation.register.view.CropProfileImageScreen
import com.peekr.presentation.register.view.RegisterCommonScreen
import com.peekr.presentation.register.viewmodel.RegisterViewModel
import com.peekr.presentation.shared.RegisterGraph
import com.peekr.presentation.shared.SubGraph
import com.peekr.presentation.shared.image.cropper.SinglePhotoPicker
import com.peekr.presentation.shared.util.event.LaunchedUiEffectHandler
import com.peekr.presentation.shared.util.sharedViewModel

fun NavGraphBuilder.registerNavigation(navController: NavHostController) {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.DisplayId) {
        composable<RegisterGraph.DisplayId> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val displayIdState by registerViewModel.displayIdState.collectAsStateWithLifecycle()

            LaunchedUiEffectHandler(
                effectFlow = registerViewModel.registerEventState,
                onConsumeEffect = { registerViewModel.onConsumeEventState() },
                onEffect = { effect ->
                    if (effect.navigateToNextScreen) {
                        navController.navigate(RegisterGraph.Name)
                    }
                },
            )

            RegisterCommonScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                title = R.string.register_screen_display_id_title,
                subTitle = R.string.register_screen_display_id_sub_title,
                placeholder = R.string.register_screen_display_id_placeholder,
                text = displayIdState.displayId,
                onTextChanged = registerViewModel::onDisplayIdChanged,
                errorMessage = displayIdState.displayIdError?.asString(),
                loading = displayIdState.loading,
                enabledNext = displayIdState.canNext,
                onNextWithValue = { displayId ->
                    registerViewModel.checkDisplayIdExists(displayId)
                },
            )
        }

        composable<RegisterGraph.Name> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val nameState by registerViewModel.nameState.collectAsStateWithLifecycle()

            RegisterCommonScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                title = R.string.register_screen_name_title,
                subTitle = R.string.register_screen_name_sub_title,
                placeholder = R.string.register_screen_name_placeholder,
                text = nameState.name,
                onTextChanged = registerViewModel::onNameChanged,
                errorMessage = nameState.nameError?.asString(),
                loading = nameState.loading,
                enabledNext = nameState.canNext,
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = { _ ->
                    navController.navigate(RegisterGraph.Profile)
                },
            )
        }

        composable<RegisterGraph.Profile> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val profileState by registerViewModel.profileState.collectAsStateWithLifecycle()
            var photoPickerOpen by remember { mutableStateOf(false) }

            SinglePhotoPicker(
                open = photoPickerOpen,
                onSelected = { selectedImage ->
                    if (selectedImage != null) {
                        registerViewModel.selectOriginalImage(selectedImage)
                    }
                },
                onClose = { photoPickerOpen = false },
            )

            LaunchedUiEffectHandler(
                effectFlow = registerViewModel.registerEventState,
                onConsumeEffect = { registerViewModel.onConsumeEventState() },
                onEffect = { event ->
                    if (event.navigateToNextScreen) {
                        navController.navigate(RegisterGraph.CropProfileImage)
                    }
                },
            )

            RegisterCommonScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                title = R.string.register_screen_profile_title,
                placeholder = R.string.register_screen_profile_placeholder,
                buttonTitle = R.string.register_screen_btn_start,
                text = profileState.introduce,
                onTextChanged = registerViewModel::onIntroduceChanged,
                errorMessage = profileState.introduceError?.asString(),
                loading = profileState.loading,
                enabledNext = profileState.canNext,
                profileImage = profileState.image,
                onProfileImageClick = { photoPickerOpen = true },
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = {
                    // TODO 회원가입 완료 처리
                },
            )
        }

        composable<RegisterGraph.CropProfileImage> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, true)
            val profileState by registerViewModel.profileState.collectAsStateWithLifecycle()

            if (profileState.originalImage == null) {
                navController.popBackStack()
            }

            CropProfileImageScreen(
                modifier = Modifier.fillMaxSize(),
                image = profileState.originalImage,
                onCrop = { croppedImage ->
                    registerViewModel.selectProfileImage(croppedImage)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}
