package com.peekr.presentation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.login.view.LoginScreen
import com.peekr.presentation.login.viewmodel.LoginViewModel

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    onNavigateMain: () -> Unit,
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(loginState.loginSuccess) {
        onNavigateMain()
    }

    LoginScreen(
        modifier = modifier.fillMaxSize(),
        loginState = loginState,
        login = loginViewModel::login,
    )
}
