package com.peekr.presentation.register

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.presentation.R
import com.peekr.presentation.register.view.RegisterScreenFrame
import com.peekr.presentation.register.viewmodel.RegisterViewModel
import com.peekr.presentation.shared.RegisterGraph
import com.peekr.presentation.shared.SubGraph
import com.peekr.presentation.shared.util.event.LaunchedUiEffectHandler
import com.peekr.presentation.shared.util.sharedViewModel

fun NavGraphBuilder.registerNavigation(navController: NavHostController) {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.DisplayId) {
        composable<RegisterGraph.DisplayId> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val registerState = registerViewModel.registerState.collectAsStateWithLifecycle()

            LaunchedUiEffectHandler(
                effectFlow = registerViewModel.registerEventState,
                onConsumeEffect = {
                    registerViewModel.onConsumeEventState()
                },
                onEffect = { effect ->
                    if (effect.navigateToNextScreen) {
                        navController.navigate(RegisterGraph.Name)
                    }
                },
            )

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_display_id_title,
                subTitle = R.string.register_screen_display_id_sub_title,
                placeholder = R.string.register_screen_display_id_placeholder,
                text = registerState.value.displayId,
                onTextChanged = registerViewModel::onDisplayIdChanged,
                errorMessage = registerState.value.error?.asString(),
                enabledNext = registerState.value.canNext,
                onBackPressed = {},
                onNextWithValue = { displayId ->
                    registerViewModel.checkDisplayIdExists(displayId)
                    registerViewModel.initCanNextState()
                },
            )
        }

        composable<RegisterGraph.Name> {
            val (text, onTextChanged) = rememberSaveable { mutableStateOf("") }

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_name_title,
                subTitle = R.string.register_screen_name_sub_title,
                placeholder = R.string.register_screen_name_placeholder,
                text = text,
                onTextChanged = onTextChanged,
                errorMessage = null,
                enabledNext = false,
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = { },
            )
        }

        composable<RegisterGraph.Profile> {
            // TODO: 회원가입 프로필 입력 화면
        }
    }
}
