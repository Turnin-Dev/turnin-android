package com.peekr.presentation.profile.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.peekr.presentation.shared.bottom.navigation.BottomNavigationFrame

@Composable
fun ProfileMainScreen(
    modifier: Modifier = Modifier,
    bottomNavController: NavHostController,
) {
    BottomNavigationFrame(
        modifier = modifier,
        bottomNavController = bottomNavController,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text("Profile", fontSize = 50.sp)
        }
    }
}
