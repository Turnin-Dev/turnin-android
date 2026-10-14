package com.turnin.core.data.repository

import com.turnin.core.data.source.local.datastore.DataStoreKey
import com.turnin.core.data.source.local.datastore.DataStoreManager
import com.turnin.core.domain.setting.model.AppSetting
import com.turnin.core.domain.setting.model.ThemeMode
import com.turnin.core.domain.setting.repository.SettingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingRepositoryImplTest {
    private val dataStoreManager: DataStoreManager = mockk()
    private lateinit var repository: SettingRepository

    @Before
    fun setUp() {
        every { dataStoreManager.getBooleanData(DataStoreKey.Setting.PushNotification) } returns flowOf(null)
        every { dataStoreManager.getStringData(DataStoreKey.Setting.ThemeMode) } returns flowOf(null)
        repository = SettingRepositoryImpl(dataStoreManager)
    }

    @Test
    fun `appSetting은 DataStore가 비어있을 때 기본값을 반환한다`() = runTest {
        // given
        every { dataStoreManager.getBooleanData(DataStoreKey.Setting.PushNotification) } returns flowOf(null)
        every { dataStoreManager.getStringData(DataStoreKey.Setting.ThemeMode) } returns flowOf(null)
        repository = SettingRepositoryImpl(dataStoreManager)

        // when
        val result = repository.appSetting.first()

        // then
        val expected = AppSetting(
            pushNotificationEnabled = true,
            themeMode = ThemeMode.SYSTEM,
        )
        assertEquals(expected, result)
    }

    @Test
    fun `appSetting은 DataStore에 저장된 값을 반환한다`() = runTest {
        // given
        every { dataStoreManager.getBooleanData(DataStoreKey.Setting.PushNotification) } returns flowOf(false)
        every { dataStoreManager.getStringData(DataStoreKey.Setting.ThemeMode) } returns flowOf(ThemeMode.DARK.name)
        repository = SettingRepositoryImpl(dataStoreManager)

        // when
        val result = repository.appSetting.first()

        // then
        val expected = AppSetting(
            pushNotificationEnabled = false,
            themeMode = ThemeMode.DARK,
        )
        assertEquals(expected, result)
    }

    @Test
    fun `setPushNotificationEnabled 호출 시 DataStore에 설정값이 저장된다`() = runTest {
        // given
        val enabled = false
        coEvery { dataStoreManager.saveBooleanData(any(), any()) } returns Unit
        repository = SettingRepositoryImpl(dataStoreManager)

        // when
        repository.setPushNotificationEnabled(enabled)

        // then
        coVerify {
            dataStoreManager.saveBooleanData(
                key = DataStoreKey.Setting.PushNotification,
                value = enabled,
            )
        }
    }

    @Test
    fun `setThemeMode 호출 시 DataStore에 테마 모드가 저장된다`() = runTest {
        // given
        val themeMode = ThemeMode.LIGHT
        coEvery { dataStoreManager.saveStringData(any(), any()) } returns Unit
        repository = SettingRepositoryImpl(dataStoreManager)

        // when
        repository.setThemeMode(themeMode)

        // then
        coVerify {
            dataStoreManager.saveStringData(
                key = DataStoreKey.Setting.ThemeMode,
                value = themeMode.name,
            )
        }
    }
}
