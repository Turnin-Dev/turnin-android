package com.peekr.core.presentation.common.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

/**
 * 네비게이션에서 여러 컴포저블 사이에 ViewModel을 공유할 때 사용한다.
 *
 * 대부분은 화면과 뷰모델 1:1 관계가 권장되지만, 회원가입과 같은 경우처럼 특수케이스에서만 사용한다.
 *
 * @param navController 네비게이션 컨트롤러
 * @param useHiltViewModel [hiltViewModel] 사용 여부 (false 시 기본 viewModel() 사용)
 */
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    useHiltViewModel: Boolean = true,
): T {
    val parentRoute = destination.parent?.route
    if (parentRoute == null) {
        return if (useHiltViewModel) hiltViewModel() else viewModel()
    }

    val parentEntry = remember(this) {
        try {
            navController.getBackStackEntry(parentRoute)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    if (parentEntry == null) {
        return if (useHiltViewModel) {
            hiltViewModel()
        } else {
            viewModel()
        }
    }

    val viewModel: T = if (useHiltViewModel) {
        hiltViewModel(parentEntry)
    } else {
        viewModel(parentEntry)
    }
    return viewModel
}
