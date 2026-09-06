package com.turnin.core.presentation.common.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * 여러 추적 기능이 통합된 화면 성능 추적기
 *
 * 각 파라미터로 일부만 활성화하여 사용할 수 있고 기본적으로 전부 활성화되어있다.
 *
 * @param screenName 추적할 화면 이름
 * @param enableViewTracker 화면 방문 기록 추적기
 * @param enableRenderTracker 화면 렌더링 성능 추적기
 * @param enableDwellTimeTracker 화면 체류시간 추적기
 *
 * @see ScreenViewTracker
 * @see ScreenRenderTracer
 * @see ScreenDwellTimeTracker
 */
@Composable
fun ScreenTrackers(
    screenName: String?,
    enableViewTracker: Boolean = true,
    enableRenderTracker: Boolean = true,
    enableDwellTimeTracker: Boolean = true,
) {
    screenName?.let {
        if (enableViewTracker) ScreenViewTracker(it)
        if (enableRenderTracker) ScreenRenderTracer(it)
        if (enableDwellTimeTracker) ScreenDwellTimeTracker(it)
    }
}

// // 현재 표시 중인 화면의 정규화된 이름을 반환한다.
// // NavBackStackEntry를 통해 목적지 화면을 구하며,
// // 화면 회전(Configure Change) 시 일시적으로 목적지가 null이 되는 현상을 방지한다.
// @Composable
// private fun rememberNormalizedScreenName(navController: NavController): String? {
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    var lastKnownRoute by rememberSaveable { mutableStateOf<String?>(null) }
//
//    LaunchedEffect(currentBackStackEntry?.destination?.route) {
//        currentBackStackEntry?.destination?.route?.normalizeRoute()?.let {
//            lastKnownRoute = it
//        }
//    }
//
//    return currentBackStackEntry?.destination?.route?.normalizeRoute() ?: lastKnownRoute
// }
//
// // 경로 문자열을 정규화하여 식별가능한 최소한의 경로(화면이름)만 남겨 반환한다.
// // SubGraph, Screens 등 네비게이션 파일이 위치한 경로에 의존한다.
// private fun String?.normalizeRoute(): String? =
//    this
//        ?.substringBefore("/") // path 인자 제거
//        ?.substringBefore("?") // query 인자 제거
//        ?.substringAfter("navigation.") // 패키지 제거
//        ?.split(".") // 클래스 세그먼트 분리
//        ?.filterNot { it in setOf("SubGraph", "Screens") } // 접두어(SubGraph, Screens) 제외
//        ?.joinToString("_") { it.replace(Regex("(?<=[a-z0-9])(?=[A-Z])"), "_") } // camelCase -> snake_case 경계 분리
//        ?.lowercase()
