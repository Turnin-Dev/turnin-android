package com.peekr.core.presentation.util

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * 이 컴포저블을 사용하게 되면, 특정 회전 방향로 화면을 잠글 수 있다.
 *
 * 기본 값은 `**ActivityInfo.SCREEN_ORIENTATION_PORTRAIT**`이므로 세로 방향으로 잠겨 있다.
 *
 * @param orientation `ActivityInfo` 값을 이용한다.
 */
@Composable
fun LockScreenOrientation(orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
    // 현재 Context를 얻어와 Activity에 접근한다.
    val context = LocalContext.current
    val activity = context as Activity

    DisposableEffect(Unit) {
        // 현재 Activity의 원해 회전 설정을 저장한다.
        val originalOrientation = activity.requestedOrientation

        // 컴포저블이 화면에 나타날 때, 원하는 회전으로 설정하여 잠근다.
        activity.requestedOrientation = orientation

        // 컴포저블이 화면에서 사라질 때, 원래 설정으로 복원한다.
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}
