package com.turnin.core.presentation.common.analytics

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turnin.core.domain.util.analytics.AnalyticsEvent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ScreenViewTrackerTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeTracker = FakeAnalyticsTracker()

    /**
     * NavHost 안에 home/detail 두 destination을 두고,
     * 각 destination 진입 시 ScreenViewTracker를 호출하도록 구성.
     * navController를 밖으로 꺼내서 테스트에서 직접 navigate() 호출용으로 사용.
     */
    private lateinit var navControllerRef: androidx.navigation.NavHostController

    private fun setNavHostContent() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalAnalyticsTracker provides fakeTracker) {
                val navController = rememberNavController()
                navControllerRef = navController

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        ScreenViewTracker(screenName = "home")
                    }
                    composable("detail") {
                        ScreenViewTracker(screenName = "detail")
                    }
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `home 최초 진입 시 SCREEN_VIEW가 1회 발행된다`() {
        setNavHostContent()

        assertEquals(1, fakeTracker.events.size)
        assertEquals("home", (fakeTracker.events[0] as AnalyticsEvent.ScreenView).screenName)
    }

    @Test
    fun `home to detail 이동 시 각각 1회씩 발행된다`() {
        setNavHostContent()

        composeTestRule.runOnUiThread { navControllerRef.navigate("detail") }
        composeTestRule.waitForIdle()

        assertEquals(2, fakeTracker.events.size)
        assertEquals("detail", (fakeTracker.events[1] as AnalyticsEvent.ScreenView).screenName)
    }
}
