package com.peekr.presentation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peekr.presentation.login.view.LoginScreen
import com.peekr.presentation.login.viewmodel.LoginViewModel
import timber.log.Timber

@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    onNavigateMain: () -> Unit,
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(loginState.loginSuccess) {
        if (loginState.loginSuccess) {
            onNavigateMain()
        }
        // 초기화 구문 필요 or onNavigateMain() 에서 백스택 제거 필요
    }

    LaunchedEffect(loginState.error) {
        loginState.error?.let { error ->
            Timber.d(error.asString(context))
        }
    }

    LoginScreen(
        modifier = modifier.fillMaxSize(),
        loginState = loginState,
        login = loginViewModel::login,
    )
}
