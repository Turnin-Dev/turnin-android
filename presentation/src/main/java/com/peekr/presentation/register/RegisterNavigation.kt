package com.peekr.presentation.register

import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.image.cropper.SinglePhotoPicker
import com.peekr.core.presentation.navigation.RegisterGraph
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.core.presentation.util.LaunchedUiEffectHandler
import com.peekr.core.presentation.util.sharedViewModel
import com.peekr.presentation.R
import com.peekr.presentation.register.view.CropProfileImageScreen
import com.peekr.presentation.register.view.RegisterCommonScreen
import com.peekr.presentation.register.viewmodel.RegisterViewModel
import kotlin.reflect.KType

fun NavGraphBuilder.registerNavigation(navController: NavHostController) {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.DisplayId) {
        animatedComposable<RegisterGraph.DisplayId> { backStackEntry ->
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

        animatedComposable<RegisterGraph.Name> { backStackEntry ->
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

        animatedComposable<RegisterGraph.Profile> { backStackEntry ->
            val registerEntry = remember(backStackEntry) {
                navController.getBackStackEntry<SubGraph.Register>()
            }
            val registerArgs = registerEntry.toRoute<SubGraph.Register>()
            val argProvider = registerArgs.provider
            val argProviderId = registerArgs.providerId
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

            val context = LocalContext.current
            LaunchedUiEffectHandler(
                effectFlow = registerViewModel.registerEventState,
                onConsumeEffect = { registerViewModel.onConsumeEventState() },
                onEffect = { event ->
                    when {
                        event.navigateToNextScreen -> {
                            // 메인 페이지로 이동
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        }

                        event.navigateToCropImageScreen -> {
                            navController.navigate(RegisterGraph.CropProfileImage)
                        }
                    }
                },
            )

            RegisterCommonScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PeekrTheme.colorScheme.backgroundNormal),
                title = R.string.register_screen_profile_title,
                subTitle = R.string.register_screen_profile_sub_title,
                placeholder = R.string.register_screen_profile_placeholder,
                buttonTitle = R.string.register_screen_btn_start,
                text = profileState.introduce,
                onTextChanged = registerViewModel::onIntroduceChanged,
                errorMessage = profileState.introduceError?.asString(),
                singleLine = false,
                loading = profileState.loading,
                enabledNext = profileState.canNext,
                profileImage = profileState.image,
                onProfileImageClick = { photoPickerOpen = true },
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = {
                    registerViewModel.register(
                        provider = argProvider,
                        providerId = argProviderId,
                        image = profileState.image,
                    )
                },
            )
        }

        animatedComposable<RegisterGraph.CropProfileImage> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, true)
            val profileState by registerViewModel.profileState.collectAsStateWithLifecycle()

            LaunchedEffect(profileState.originalImage) {
                if (profileState.originalImage == null) {
                    navController.popBackStack()
                }
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

private inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards SizeTransform?)? = null,
    noinline content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
) {
    composable<T>(
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } },
        typeMap = typeMap,
        deepLinks = deepLinks,
        sizeTransform = sizeTransform,
        content = content,
    )
}
