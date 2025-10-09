package com.peekr.core.presentation.navigation.bottom

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

/**
 * 바텀 네비게이션 각 탭에서 공통적으로 사용하는 프레임
 *
 * @param modifier [Modifier]
 * @param bottomNavController 바텀 네비게이션의 navController
 * @param content 각 탭의 [NavHost]를 구현한다. (반드시 패딩 파라미터를 적용해야 하지만 중첩 Scaffold 구조에선 예외이다.)
 */
@Composable
fun BottomNavigationFrame(
    modifier: Modifier = Modifier,
    bottomNavController: NavHostController,
    content: @Composable ((PaddingValues) -> Unit),
) {
    Scaffold(
        modifier = modifier,
        bottomBar = { BottomNavigationBar(Modifier.fillMaxWidth(), bottomNavController) },
    ) { innerPadding ->
        content(
            PaddingValues(bottom = innerPadding.calculateBottomPadding()),
        )
    }
}
