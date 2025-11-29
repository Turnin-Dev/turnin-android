package com.peekr.presentation.profile.view.modal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.peekr.presentation.profile.view.ProfileScreenFrame

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
            },
            profile = {
            },
            keywordGraph = {
            },
        )
    }
}
