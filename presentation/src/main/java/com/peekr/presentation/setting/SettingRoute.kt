package com.peekr.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.presentation.setting.view.SettingScreen
import com.peekr.presentation.setting.viewmodel.SettingViewModel

@Composable
fun SettingRoute(
    onBackPressed: () -> Unit,
) {
    val viewModel: SettingViewModel = hiltViewModel()

    SettingScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(PeekrTheme.colorScheme.backgroundNormal),
        onBackPressed = onBackPressed,
    )
}
