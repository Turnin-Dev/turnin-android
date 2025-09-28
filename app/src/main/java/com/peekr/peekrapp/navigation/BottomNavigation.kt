package com.peekr.peekrapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.presentation.keyword.model.UiKeyword
import com.peekr.core.presentation.navigation.SubGraph
import com.peekr.core.presentation.navigation.bottom.BottomNavigationFrame
import com.peekr.presentation.discover.main.DiscoverMainScreen
import com.peekr.presentation.home.main.HomeMainScreen
import com.peekr.presentation.profile.model.UiUserProfile
import com.peekr.presentation.profile.view.ProfileScreen

@Composable
fun BottomNavigation(
    appNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val bottomNavController = rememberNavController()

    BottomNavigationFrame(
        modifier = modifier,
        bottomNavController = bottomNavController,
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = bottomNavController,
            startDestination = SubGraph.Home,
        ) {
            composable<SubGraph.Home> {
                HomeMainScreen(
                    modifier = Modifier.fillMaxSize(),
                    onNavigateToSecond = {
                        appNavController.navigate("HomeSecond")
                    },
                )
            }

            composable<SubGraph.Discover> {
                DiscoverMainScreen(modifier = Modifier.fillMaxSize())
            }

            composable<SubGraph.Profile> {
                ProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    userProfile = UiUserProfile(
                        displayId = DisplayId("Honggd123"),
                        name = Name("홍길동"),
                        friendsTotal = 86,
                        profileImageUrl = null,
                        introduce = Introduce(
                            "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                                "1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
                        ),
                        keywords = UiKeyword.samples,
                    ),
                )
            }
        }
    }
}
