package com.peekr.peekrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.peekr.core.data.eventBus.AuthEventBus
import com.peekr.core.designsystem.component.snackbar.PeekrSnackbar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.bottom.BottomNavigationBarTokens
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.core.presentation.feature.keywordGraph.model.UiKeywordNode
import com.peekr.core.presentation.feature.keywordGraph.model.UiUserCluster
import com.peekr.core.presentation.feature.keywordGraph.model.UiUserNode
import com.peekr.core.presentation.feature.keywordGraph.view.graph.KeywordNetworkGraph
import com.peekr.core.presentation.ui.component.snackbar.SnackbarController
import com.peekr.core.presentation.ui.model.UiUserKeyword
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authEventBus: AuthEventBus

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // ------------------------------ SplashScreen ------------------------------
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoading.value || mainViewModel.loggedIn.value == null
            }
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val appNavController = rememberNavController()

            // ------------------------------ Auth Logout ------------------------------
            val navBackStackEntry by appNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isAuthScreen by remember(currentDestination?.route) {
                derivedStateOf {
                    currentDestination?.hierarchy?.any {
                        currentDestination.hasRoute(SubGraph.Login.Root::class) ||
                            currentDestination.hasRoute(SubGraph.Register.Root::class)
                    } == true
                }
            }
            if (!isAuthScreen) {
                ObserveAsEvents(
                    flow = authEventBus.logoutEvent,
                    onEvent = {
                        appNavController.navigate(SubGraph.Login.Root) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }

            // ------------------------------ Snackbar ------------------------------
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

            // ------------------------------ Main ------------------------------
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
                    contentWindowInsets = WindowInsets.systemBars,
                ) { innerPadding ->
// ------------------------------ 메인(프로덕션 용) ------------------------------
//                    val loggedIn by mainViewModel.loggedIn.collectAsStateWithLifecycle()
//
//                    AppNavigation(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        appNavController = appNavController,
//                        loggedIn = loggedIn,
//                    )

// ------------------------------ 키워드 네트워크 그래프 테스트용 2 ------------------------------
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(PeekrTheme.colorScheme.backgroundNormal),
                    ) {
                        KeywordNetworkGraph(
                            modifier = Modifier.fillMaxSize(),
                            myCluster = UiUserCluster(
                                userNode = UiUserNode(0, "Me", null),
                                keywordNodes = UiUserKeyword.samples.map {
                                    UiKeywordNode(it.id.value, it.keywordId.value, it.keywordName)
                                },
                            ),
                            otherClusters = List(500) {
                                UiUserCluster(
                                    userNode = UiUserNode(
                                        userId = (it + 1).toLong(),
                                        userName = "username$it",
                                        profileImageUrl = "https://fujifilm-korea.co.kr" +
                                            "/image/playwith/wl/dt/soliywlj/html/" +
                                            "278243925tmlm.jpg",
                                    ),
                                    keywordNodes = UiUserKeyword.samples.map {
                                        UiKeywordNode(
                                            it.id.value,
                                            it.keywordId.value,
                                            "a".repeat(15),
                                        )
                                    },
                                )
                            },
                        )
                    }

// ------------------------------ 키워드 네트워크 그래프 테스트용 1 ------------------------------
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .background(Color.White),
//                    ) {
//                        NetworkUniverse(
//                            modifier = Modifier.fillMaxSize(),
//                            myCluster = UserCluster(
//                                userId = "0",
//                                profileImageUrl = null,
//                                keywords = UiUserKeyword.samples,
//                            ),
//                            otherClusters = List(500) {
//                                UserCluster(
//                                    userId = "${it + 1}",
//                                    profileImageUrl = "https://fujifilm-korea.co.kr/image/playwith/wl/dt/soliywlj/html/278243925tmlm.jpg",
//                                    keywords = UiUserKeyword.samples,
//                                )
//                            },
//                            connections = List(500) {
//                                "0" to "${it + 1}"
//                            },
//                        )
//                    }

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
//                    NavHost(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(innerPadding),
//                        navController = appNavController,
//                        startDestination = BottomNav,
//                    ) {
//                        composable<BottomNav> {
//                            BottomNavigation(
//                                modifier = Modifier.fillMaxSize(),
//                                appNavController = appNavController,
//                            )
//                        }
//
//                        composable(route = "HomeSecond") {
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center,
//                            ) {
//                                Text("HomeSecond", fontSize = 50.sp)
//                            }
//                        }
//                    }
// ------------------------------ 전체 네비게이션 테스트용 ------------------------------
//                    val testDataViewModel: TestDataViewModel = hiltViewModel()
//                    AppNavigation(
//                        modifier = Modifier.fillMaxSize(),
//                        appNavController = appNavController,
//                    )
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
