package com.peekr.peekrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peekr.core.designsystem.component.snackbar.PeekrSnackbar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.navigation.BottomNav
import com.peekr.core.presentation.navigation.bottom.BottomNavigationBarTokens
import com.peekr.core.presentation.util.ObserveAsEvents
import com.peekr.core.presentation.util.SnackbarController
import com.peekr.peekrapp.navigation.BottomNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appNavController = rememberNavController()

            // ------------------------------ 스낵바 ------------------------------
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            ObserveAsEvents(
                flow = SnackbarController.events,
                key1 = snackbarHostState,
                onEvent = { event ->
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        val result =
                            snackbarHostState.showSnackbar(
                                message = event.message.asString(context),
                                actionLabel = event.action?.name,
                                duration = SnackbarDuration.Short,
                            )

                        if (result == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                    }
                },
            )

            PeekrAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PeekrTheme.colorScheme.backgroundNormal,
                    snackbarHost = {
                        PeekrSnackbar(
                            modifier = Modifier
                                .padding(bottom = BottomNavigationBarTokens.MinHeightDp),
                            snackbarHostState = snackbarHostState,
                        )
                    },
                ) { innerPadding ->
// ------------------------------ 메인(프로덕션 용) ------------------------------
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
                    val testDataViewModel: TestDataViewModel = hiltViewModel()
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = appNavController,
                        startDestination = BottomNav,
                    ) {
                        composable<BottomNav> {
                            BottomNavigation(
                                modifier = Modifier.fillMaxSize(),
                                appNavController = appNavController,
                            )
                        }

                        composable(route = "HomeSecond") {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("HomeSecond", fontSize = 50.sp)
                            }
                        }
                    }
// ------------------------------ 키워드 그래프 테스트용 ------------------------------
//                    Box(Modifier.padding(innerPadding)) {
//                        KeywordGraphView(
//                            modifier = Modifier.fillMaxSize(),
//                            profileImageUrl = null,
//                            keywords = UiKeyword.samples,
//                        )
//                    }
                }
            }
        }
    }
}
