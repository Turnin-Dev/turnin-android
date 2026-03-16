package com.peekr.presentation.setting.viewmodel

import com.peekr.core.domain.setting.model.AppSetting
import com.peekr.core.domain.setting.model.ThemeMode
import com.peekr.core.domain.setting.repository.SettingRepository
import com.peekr.core.presentation.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingRepository: SettingRepository = mockk()
    private lateinit var viewModel: NotificationSettingViewModel

    private val fakeAppSetting = MutableStateFlow(
        AppSetting(
            pushNotificationEnabled = true,
            themeMode = ThemeMode.SYSTEM,
        ),
    )

    @Before
    fun setUp() {
        every { settingRepository.appSetting } returns fakeAppSetting
        viewModel = NotificationSettingViewModel(settingRepository)
    }

    @Test
    fun `초기 앱 설정 상태가 올바르게 로드된다`() = runTest {
        // given
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.appSetting.collect()
        }

        // then
        assertEquals(fakeAppSetting.value, viewModel.appSetting.value)
    }

    @Test
    fun `저장소의 앱 설정이 변경되면 뷰모델의 상태도 업데이트된다`() = runTest {
        // given
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.appSetting.collect()
        }
        val updatedSetting = AppSetting(
            pushNotificationEnabled = false,
            themeMode = ThemeMode.DARK,
        )

        // when
        fakeAppSetting.value = updatedSetting

        // then
        assertEquals(updatedSetting, viewModel.appSetting.value)
    }

    @Test
    fun `푸시 알림 토글 시 저장소의 설정 함수가 호출된다`() = runTest {
        // given
        val enabled = false
        coEvery { settingRepository.setPushNotificationEnabled(enabled) } returns Unit

        // when
        viewModel.togglePushNotification(enabled)

        // then
        coVerify { settingRepository.setPushNotificationEnabled(enabled) }
    }
}
