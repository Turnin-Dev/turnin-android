package com.peekr.peekrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.presentation.shared.SubGraph
import com.peekr.presentation.shared.bottom.navigation.bottomNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appNavController = rememberNavController()

            PeekrAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PeekrTheme.colorScheme.backgroundNormal,
                ) { innerPadding ->
// ------------------------------ 메인 ------------------------------
//                    MainNavigation(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        appNavController = appNavController
//                    )

// ------------------------------ 회원가입 테스트용 ------------------------------
//                    NavHost(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        navController = appNavController,
//                        startDestination = "test-start",
//                    ) {
//                        composable(route = "test-start") {
//                            LaunchedEffect(Unit) {
//                                appNavController
//                                    .navigate(
//                                        SubGraph.Register(
//                                            provider = UiSocialLoginProvider.GOOGLE,
//                                            providerId = "asdasasd",
//                                        ),
//                                    )
//                            }
//                        }
//
//                        registerNavigation(
//                            navController = appNavController,
//                        )
//                    }

// ------------------------------ 바텀 네비게이션 테스트용 ------------------------------
                    val bottomNavController = rememberNavController()
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = bottomNavController,
                        startDestination = SubGraph.Home,
                    ) {
                        bottomNavigation(bottomNavController)
                    }
                }
            }
        }
    }
}
