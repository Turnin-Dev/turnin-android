package com.peekr.presentation.home.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.peekr.core.presentation.navigation.bottom.BottomNavigationFrame

@Composable
fun HomeMainScreen(
    modifier: Modifier = Modifier,
    bottomNavController: NavHostController,
    onNavigateToSecond: () -> Unit,
) {
    BottomNavigationFrame(
        modifier = modifier,
        bottomNavController = bottomNavController,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(100.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Home", fontSize = 50.sp)
            Button(onClick = onNavigateToSecond) {
                Text("Next")
            }
        }
    }
}
