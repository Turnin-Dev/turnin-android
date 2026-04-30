package com.turnin.designsystem.util.click

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.util.click.ClickMode
import com.turnin.core.designsystem.util.click.clickableSingle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClickableSingleTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val clickedCount = mutableStateOf(0)

    @Before
    fun setup() {
        clickedCount.value = 0
    }

    @Test
    fun throttleClickable_test() {
        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .testTag("throttleBox")
                    .clickableSingle(clickMode = ClickMode.Throttle, delayTimeMs = TEST_TIME_MS) {
                        clickedCount.value++
                    }.size(100.dp)
                    .background(Color.Red),
            )
        }

        // 빠르게 3번 클릭
        repeat(3) {
            composeTestRule.onNodeWithTag("throttleBox").performClick()
        }

        // TEST_TIME_MS 내 중복 클릭이 무시되었는지 확인
        composeTestRule.runOnIdle {
            assertEquals(1, clickedCount.value)
        }
    }

    @Test
    fun debounceClickable_test() {
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .testTag("debounceBox")
                    .clickableSingle(clickMode = ClickMode.Debounce, delayTimeMs = TEST_TIME_MS) {
                        clickedCount.value++
                    }.size(100.dp)
                    .background(Color.Blue),
            )
        }

        // 100ms 간격으로 3번 클릭 (debounceTime은 TEST_TIME_MS 기준)
        composeTestRule.onNodeWithTag("debounceBox").performClick()
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithTag("debounceBox").performClick()
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithTag("debounceBox").performClick()

        // 아직 TEST_TIME_MS만큼 시간이 흐르지 않았으므로 이벤트 실행 안됨
        composeTestRule.runOnIdle {
            assertEquals(0, clickedCount.value)
        }

        // TEST_TIME_MS 시간 경과시 이벤트 실행
        composeTestRule.mainClock.advanceTimeBy(TEST_TIME_MS)

        composeTestRule.runOnIdle {
            assertEquals(1, clickedCount.value)
        }
    }

    companion object {
        private const val TEST_TIME_MS = 300L
    }
}
