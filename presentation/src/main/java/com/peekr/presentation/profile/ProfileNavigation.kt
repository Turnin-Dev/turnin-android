package com.peekr.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.peekr.core.presentation.navigation.SubGraph

fun NavGraphBuilder.profileNavigation() {
    composable<SubGraph.Profile> {
        ProfileRoute()
    }
}
