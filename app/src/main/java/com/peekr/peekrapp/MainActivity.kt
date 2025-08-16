package com.peekr.peekrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.presentation.register.registerNavigation
import com.peekr.presentation.shared.SubGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainNavController = rememberNavController()

            PeekrAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PeekrTheme.colorScheme.backgroundNormal,
                ) { innerPadding ->
                    // 메인
//                    MainNavigation(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        mainNavController = mainNavController,
//                    )

                    // 테스트용
                    NavHost(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        navController = mainNavController,
                        startDestination = SubGraph.Register,
                    ) {
                        registerNavigation(navController = mainNavController)
                    }
                }
            }
        }
    }
}
