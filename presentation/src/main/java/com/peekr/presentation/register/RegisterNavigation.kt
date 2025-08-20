package com.peekr.presentation.register

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            val displayIdState by registerViewModel.displayIdState.collectAsStateWithLifecycle()

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
                text = displayIdState.displayId,
                onTextChanged = registerViewModel::onDisplayIdChanged,
                errorMessage = displayIdState.displayIdError?.asString(),
                enabledNext = displayIdState.canNext,
                onNextWithValue = { displayId ->
                    registerViewModel.checkDisplayIdExists(displayId)
                },
            )
        }

        composable<RegisterGraph.Name> { backStackEntry ->
            val registerViewModel: RegisterViewModel =
                backStackEntry.sharedViewModel(navController, useHiltViewModel = true)
            val nameState = registerViewModel.nameState.collectAsStateWithLifecycle()

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_name_title,
                subTitle = R.string.register_screen_name_sub_title,
                placeholder = R.string.register_screen_name_placeholder,
                text = nameState.value.name,
                onTextChanged = registerViewModel::onNameChanged,
                errorMessage = nameState.value.nameError?.asString(),
                enabledNext = nameState.value.canNext,
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = { _ ->
                    navController.navigate(RegisterGraph.Profile)
                },
            )
        }

        composable<RegisterGraph.Profile> {
            val (text, onTextChanged) = remember { mutableStateOf("") }

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_profile_title,
                placeholder = R.string.register_screen_profile_placeholder,
                text = text,
                onTextChanged = onTextChanged,
                errorMessage = null,
                enabledNext = false,
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = { },
            )
        }
    }
}
