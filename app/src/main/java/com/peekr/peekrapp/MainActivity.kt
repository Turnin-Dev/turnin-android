package com.peekr.peekrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.peekr.peekrapp.navigation.MainNavigation
import com.peekr.peekrapp.ui.theme.PeekrTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainNavController = rememberNavController()

            PeekrTheme {
                MainNavigation(
                    modifier = Modifier.fillMaxSize(),
                    mainNavController = mainNavController,
                )
            }
        }
    }
}
