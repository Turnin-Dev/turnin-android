package com.turnin.core.presentation.common.analytics

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
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
class ScreenDwellTimeTrackerTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeTracker = FakeAnalyticsTracker()
    private val fakeClock = FakeClock()
    private lateinit var navControllerRef: NavHostController

    private fun setNavHostContent() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalAnalyticsTracker provides fakeTracker) {
                val navController = rememberNavController()
                navControllerRef = navController

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        ScreenDwellTimeTracker(screenName = "home", nowMs = fakeClock::now)
                    }
                    composable("detail") {
                        ScreenDwellTimeTracker(screenName = "detail", nowMs = fakeClock::now)
                    }
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun advance(ms: Long) = fakeClock.advance(ms)

    @Test
    fun `home에서 detail로 이동하면 정확한 체류시간이 1회 전송된다`() {
        setNavHostContent()

        advance(1_200L)

        composeTestRule.runOnUiThread { navControllerRef.navigate("detail") }
        composeTestRule.waitForIdle()

        assertEquals(1, fakeTracker.events.size)
        val event = fakeTracker.events[0] as AnalyticsEvent.ScreenDwellTime
        assertEquals("home", event.screenName)
        assertEquals(1_200L, event.dwellTimeMs)
    }

    @Test
    fun `home 재방문 시 이전 체류시간이 섞이지 않고 0부터 새로 누적된다`() {
        setNavHostContent()

        advance(1_200L)
        composeTestRule.runOnUiThread { navControllerRef.navigate("detail") }
        composeTestRule.waitForIdle()
        assertEquals(1_200L, (fakeTracker.events[0] as AnalyticsEvent.ScreenDwellTime).dwellTimeMs)

        advance(300L)
        composeTestRule.runOnUiThread { navControllerRef.popBackStack("home", inclusive = false) }
        composeTestRule.waitForIdle()

        advance(700L)
        composeTestRule.runOnUiThread { navControllerRef.navigate("detail") }
        composeTestRule.waitForIdle()

        assertEquals(3, fakeTracker.events.size)
        val secondHomeEvent = fakeTracker.events[2] as AnalyticsEvent.ScreenDwellTime
        assertEquals("home", secondHomeEvent.screenName)
        assertEquals(700L, secondHomeEvent.dwellTimeMs)
    }

    @Test
    fun `ON_STOP 후 ON_START 사이 백그라운드 시간은 누적에서 제외된다`() {
        setNavHostContent()

        advance(500L)

        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED) // ON_STOP
        advance(10_000L) // 백그라운드 (누적되면 안 됨)
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED) // ON_START
        composeTestRule.waitForIdle()

        advance(300L)

        composeTestRule.runOnUiThread { navControllerRef.navigate("detail") }
        composeTestRule.waitForIdle()

        val event = fakeTracker.events[0] as AnalyticsEvent.ScreenDwellTime
        assertEquals(800L, event.dwellTimeMs) // 500 + 300
    }

    @Test
    fun `ON_STOP 직후 회전 없이 앱이 종료되어도 중복 누적되지 않는다`() {
        // 회귀 테스트: segmentStartMs 리셋이 없으면 중복 누적되어 약 2배가 됨
        setNavHostContent()

        advance(1_000L)
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED) // ON_STOP

        advance(5_000L) // 백그라운드 상태 시간 경과 (누적되면 안 됨)
        composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        composeTestRule.waitForIdle()

        assertEquals(1, fakeTracker.events.size)
        val event = fakeTracker.events[0] as AnalyticsEvent.ScreenDwellTime
        assertEquals(1_000L, event.dwellTimeMs) // 2000L이 아니라 정확히 1000L
    }
}
