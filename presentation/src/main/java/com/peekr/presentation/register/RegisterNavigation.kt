package com.peekr.presentation.register

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.peekr.presentation.shared.RegisterGraph
import com.peekr.presentation.shared.SubGraph

fun NavGraphBuilder.registerNavigation() {
    navigation<SubGraph.Register>(startDestination = RegisterGraph.Name) {
        composable<RegisterGraph.Name> {
            // TODO: 회원가입 이름 입력 화면
        }

        composable<RegisterGraph.Nickname> {
            // TODO: 회원가입 닉네임 입력 화면
        }

        composable<RegisterGraph.Profile> {
            // TODO: 회원가입 프로필 입력 화면
        }
    }
}
