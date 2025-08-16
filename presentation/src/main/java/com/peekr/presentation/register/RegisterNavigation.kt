package com.peekr.presentation.register

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.presentation.R
import com.peekr.presentation.register.view.RegisterScreenFrame
import com.peekr.presentation.shared.RegisterGraph
import com.peekr.presentation.shared.SubGraph

fun NavGraphBuilder.registerNavigation(navController: NavHostController) {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.Name) {
        composable<RegisterGraph.Name> {
            var (text, onTextChanged) = rememberSaveable { mutableStateOf("") }

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_name_title,
                placeholder = R.string.register_screen_name_placeholder,
                text = text,
                onTextChanged = onTextChanged,
                errorMessage = null,
                onBackPressed = {},
                onNextWithValue = { navController.navigate(RegisterGraph.Nickname) },
            )
        }

        composable<RegisterGraph.Nickname> {
            var (text, onTextChanged) = rememberSaveable { mutableStateOf("") }

            RegisterScreenFrame(
                modifier = Modifier.fillMaxSize(),
                title = R.string.register_screen_nickname_title,
                placeholder = R.string.register_screen_nickname_placeholder,
                text = text,
                onTextChanged = onTextChanged,
                errorMessage = null,
                onBackPressed = { navController.popBackStack() },
                onNextWithValue = { },
            )
        }

        composable<RegisterGraph.Profile> {
            // TODO: 회원가입 프로필 입력 화면
        }
    }
}
