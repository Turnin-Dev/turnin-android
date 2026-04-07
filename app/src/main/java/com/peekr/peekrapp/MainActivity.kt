package com.peekr.peekrapp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.peekr.core.designsystem.component.snackbar.PeekrSnackbar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.presentation.common.navigation.SubGraph
import com.peekr.core.presentation.common.navigation.bottom.BottomNavigationBarTokens
import com.peekr.core.presentation.common.navigation.navigateToLogin
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.util.ObserveAsEvents
import com.peekr.peekrapp.navigation.AppNavigation
import com.peekr.peekrapp.util.notification.NotificationPermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var snackbarController: SnackbarController

    @Inject
    lateinit var notificationPermissionManager: NotificationPermissionManager

    private val mainViewModel: MainViewModel by viewModels()

    // 알림 동기화 중복 요청 방지 플래그
    private var isFromSystemSetting = false

    // 권한 요청 런처
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            // 허용 / 두 번 거부 / 설정에서 돌아온 경우 모두 그냥 sync
            mainViewModel.syncNotificationState()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            // 한 번 거부 → 설정으로 유도
            navigateToSystemNotificationSetting()
        }
    }

    companion object {
        private const val KEY_FROM_SYSTEM_SETTING = "isFromSystemSetting"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_FROM_SYSTEM_SETTING, isFromSystemSetting)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // ------------------------------ SplashScreen ------------------------------
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoading.value || mainViewModel.loggedIn.value == null
            }
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        isFromSystemSetting = savedInstanceState?.getBoolean(KEY_FROM_SYSTEM_SETTING) ?: false

        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val appNavController = rememberNavController()
            val navBackStackEntry by appNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // ------------------------------ Handle DeepLink ------------------------------
            DisposableEffect(appNavController) {
                val consumer = Consumer<Intent> {
                    appNavController.handleDeepLink(it)
                }
                this@MainActivity.addOnNewIntentListener(consumer)
                onDispose {
                    this@MainActivity.removeOnNewIntentListener(consumer)
                }
            }

            // ------------------------------ Auth Logout ------------------------------
            val isAuthScreen by remember(currentDestination?.route) {
                derivedStateOf {
                    currentDestination?.hierarchy?.any { destination ->
                        destination.hasRoute(SubGraph.Login.Root::class) ||
                            destination.hasRoute(SubGraph.Register.Root::class)
                    } == true
                }
            }
            if (!isAuthScreen) {
                val logoutMessage = stringResource(R.string.auto_logout)
                ObserveAsEvents(mainViewModel.navigateToLogin) {
                    Toast.makeText(context, logoutMessage, Toast.LENGTH_SHORT).show()
                    // 로그인 화면으로 이동
                    appNavController.navigateToLogin()
                }
            }

            // ------------------------------ Snackbar ------------------------------
            val snackbarHostState = remember(isAuthScreen) { SnackbarHostState() }
            val snackbarBottomPadding = remember {
                derivedStateOf {
                    if (navBackStackEntry?.destination?.hasRoute<SubGraph.BottomNav.Root>() == true) {
                        BottomNavigationBarTokens.MinHeightDp
                    } else {
                        0.dp
                    }
                }
            }

            ObserveAsEvents(
                flow = snackbarController.events,
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
                        if (!isAuthScreen) {
                            PeekrSnackbar(
                                modifier = Modifier.padding(bottom = snackbarBottomPadding.value),
                                snackbarHostState = snackbarHostState,
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets.systemBars,
                ) { innerPadding ->
                    val loggedIn by mainViewModel.loggedIn.collectAsStateWithLifecycle()

                    AppNavigation(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        appNavController = appNavController,
                        loggedIn = loggedIn,
                        onCheckPermission = {
                            // 알림 권한 요청
                            requestNotificationPermissionIfNeeded()
                        },
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        // Android 13 미만은 권한 요청 불필요
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        // 이미 권한 있으면 스킵
        if (notificationPermissionManager.hasPermission()) return

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun navigateToSystemNotificationSetting() {
        isFromSystemSetting = true
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            startActivity(this)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_POINTER_DOWN) return true
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        // 설정 앱에서 권한 변경 후 복귀 시에만 sync
        if (isFromSystemSetting) {
            isFromSystemSetting = false
            mainViewModel.syncNotificationState()
        }
    }

    // 앱이 백그라운드에서 포그라운드로 올 때
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
