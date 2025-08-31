package com.peekr.presentation.shared.bottom.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.peekr.presentation.shared.DiscoverGraph
import com.peekr.presentation.shared.HomeGraph
import com.peekr.presentation.shared.ProfileGraph
import com.peekr.presentation.shared.SubGraph

fun NavGraphBuilder.bottomNavigation(navController: NavHostController) {
    navigation<SubGraph.BottomNav>(startDestination = SubGraph.Home) {
        navigation<SubGraph.Home>(startDestination = HomeGraph.Main) {
            composable<HomeGraph.Main> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Home", fontSize = 50.sp)
                }
            }
        }

        navigation<SubGraph.Discover>(startDestination = DiscoverGraph.Main) {
            composable<DiscoverGraph.Main> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Discover", fontSize = 50.sp)
                }
            }
        }

        navigation<SubGraph.Profile>(startDestination = ProfileGraph.Main) {
            composable<ProfileGraph.Main> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Profile", fontSize = 50.sp)
                }
            }
        }
    }
}
