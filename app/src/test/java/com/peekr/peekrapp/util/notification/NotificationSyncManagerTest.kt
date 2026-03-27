package com.peekr.peekrapp.util.notification

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.core.domain.setting.model.AppSetting
import com.peekr.core.domain.setting.model.NotificationSyncState
import com.peekr.core.domain.setting.model.ThemeMode
import com.peekr.core.domain.setting.repository.SettingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSyncManagerTest {
    private val notificationPermissionManager: NotificationPermissionManager = mockk()
    private val notificationRepository: NotificationRepository = mockk()
    private val settingRepository: SettingRepository = mockk()
    private lateinit var syncManager: NotificationSyncManagerImpl

    @Before
    fun setUp() {
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = true, themeMode = ThemeMode.SYSTEM),
        )
    }

    private fun createSyncManager() {
        syncManager = NotificationSyncManagerImpl(
            notificationPermissionManager = notificationPermissionManager,
            notificationRepository = notificationRepository,
            settingRepository = settingRepository,
        )
    }

    // ========================
    // sync() - 상태 변경 없음 (스킵)
    // ========================

    @Test
    fun `sync - 권한 있고 알림 활성화 상태에서 이미 REGISTERED면 스킵한다`() = runTest {
        // given
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = true, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns true
        coEvery {
            notificationRepository.getNotificationSyncState()
        } returns NotificationSyncState.REGISTERED

        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.registerFcmToken(any()) }
        coVerify(exactly = 0) { notificationRepository.deactivateFcmToken(any()) }
    }

    @Test
    fun `sync - 권한 없고 알림 비활성화 상태에서 이미 DEACTIVATED면 스킵한다`() = runTest {
        // given
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns false
        coEvery {
            notificationRepository.getNotificationSyncState()
        } returns NotificationSyncState.DEACTIVATED

        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.registerFcmToken(any()) }
        coVerify(exactly = 0) { notificationRepository.deactivateFcmToken(any()) }
    }

    // ========================
    // sync() -> registerTokenAndSubscribe()
    // ========================

    @Test
    fun `sync - shouldRegister가 true이고 FCM 토큰 등록 성공 시 상태를 REGISTERED로 저장한다`() = runTest {
        // given
        val token = "fcm_token"
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = true, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns true
        coEvery { notificationRepository.getNotificationSyncState() } returns null
        coEvery { notificationRepository.getFcmToken() } returns token
        coEvery { notificationRepository.registerFcmToken(token) } returns Result.Success(Unit)
        coEvery { notificationRepository.setNotificationSyncState(any()) } returns Unit
        coEvery { notificationRepository.subscribeToTopic() } returns Unit

        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify { notificationRepository.registerFcmToken(token) }
        coVerify { notificationRepository.setNotificationSyncState(NotificationSyncState.REGISTERED) }
        coVerify { notificationRepository.subscribeToTopic() }
    }

    @Test
    fun `sync - shouldRegister가 true이고 FCM 토큰 등록 실패 시 상태를 저장하지 않는다`() = runTest {
        // given
        val token = "fcm_token"
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = true, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns true
        coEvery { notificationRepository.getNotificationSyncState() } returns null
        coEvery { notificationRepository.getFcmToken() } returns token
        coEvery {
            notificationRepository.registerFcmToken(token)
        } returns Result.Error(CommonErrorType.Unexpected(null))

        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.setNotificationSyncState(any()) }
        coVerify(exactly = 0) { notificationRepository.subscribeToTopic() }
    }

    @Test
    fun `sync - shouldRegister가 true이고 FCM 토큰 발급 실패 시 등록을 시도하지 않는다`() = runTest {
        // given
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = true, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns true
        coEvery { notificationRepository.getNotificationSyncState() } returns null
        coEvery { notificationRepository.getFcmToken() } returns null

        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.registerFcmToken(any()) }
        coVerify(exactly = 0) { notificationRepository.setNotificationSyncState(any()) }
    }

    // ========================
    // sync() -> unsubscribe()
    // ========================

    @Test
    fun `sync - shouldRegister가 false이고 REGISTERED 상태일 때 FCM 토큰 비활성화 성공 시 상태를 DEACTIVATED로 저장한다`() =
        runTest {
            // given
            val token = "fcm_token"
            every { settingRepository.appSetting } returns flowOf(
                AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
            )
            coEvery { notificationPermissionManager.hasPermission() } returns false
            coEvery { notificationRepository.getNotificationSyncState() } returns NotificationSyncState.REGISTERED
            coEvery { notificationRepository.getFcmToken() } returns token
            coEvery { notificationRepository.deactivateFcmToken(token) } returns Result.Success(Unit)
            coEvery { notificationRepository.setNotificationSyncState(any()) } returns Unit
            coEvery { notificationRepository.unsubscribeFromTopic() } returns Unit
            createSyncManager()

            // when
            syncManager.sync()

            // then
            coVerify { notificationRepository.deactivateFcmToken(token) }
            coVerify { notificationRepository.setNotificationSyncState(NotificationSyncState.DEACTIVATED) }
            coVerify { notificationRepository.unsubscribeFromTopic() }
        }

    @Test
    fun `sync - shouldRegister가 false이고 DEACTIVATED 상태면 deactivate를 스킵한다`() = runTest {
        // given
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns false
        coEvery { notificationRepository.getNotificationSyncState() } returns NotificationSyncState.DEACTIVATED
        createSyncManager()

        // when
        syncManager.sync()

        // then - 이미 해제됨, sync() 최상단 스킵 조건에서 걸림
        coVerify(exactly = 0) { notificationRepository.deactivateFcmToken(any()) }
        coVerify(exactly = 0) { notificationRepository.setNotificationSyncState(any()) }
    }

    @Test
    fun `sync - shouldRegister가 false이고 상태가 null(unknown)이면 deactivate를 시도한다`() = runTest {
        // given - 기존 앱 업데이트 or 데이터 손상으로 상태가 null인 사용자
        val token = "fcm_token"
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns false
        coEvery { notificationRepository.getNotificationSyncState() } returns null
        coEvery { notificationRepository.getFcmToken() } returns token
        coEvery { notificationRepository.deactivateFcmToken(token) } returns Result.Success(Unit)
        coEvery { notificationRepository.setNotificationSyncState(any()) } returns Unit
        coEvery { notificationRepository.unsubscribeFromTopic() } returns Unit
        createSyncManager()

        // when
        syncManager.sync()

        // then - null은 unknown이므로 서버에 토큰이 있을 수 있음, deactivate 시도해야 함
        coVerify { notificationRepository.deactivateFcmToken(token) }
        coVerify { notificationRepository.setNotificationSyncState(NotificationSyncState.DEACTIVATED) }
    }

    @Test
    fun `sync - shouldRegister가 false이고 상태가 null이며 FCM 토큰 발급 실패 시 deactivate를 시도하지 않는다`() = runTest {
        // given
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns false
        coEvery { notificationRepository.getNotificationSyncState() } returns null
        coEvery { notificationRepository.getFcmToken() } returns null
        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.deactivateFcmToken(any()) }
        coVerify(exactly = 0) { notificationRepository.setNotificationSyncState(any()) }
    }

    @Test
    fun `sync - shouldRegister가 false이고 FCM 토큰 비활성화 실패 시 상태를 저장하지 않는다`() = runTest {
        // given
        val token = "fcm_token"
        every { settingRepository.appSetting } returns flowOf(
            AppSetting(pushNotificationEnabled = false, themeMode = ThemeMode.SYSTEM),
        )
        coEvery { notificationPermissionManager.hasPermission() } returns false
        coEvery { notificationRepository.getNotificationSyncState() } returns NotificationSyncState.REGISTERED
        coEvery { notificationRepository.getFcmToken() } returns token
        coEvery {
            notificationRepository.deactivateFcmToken(token)
        } returns Result.Error(CommonErrorType.Unexpected(null))
        createSyncManager()

        // when
        syncManager.sync()

        // then
        coVerify(exactly = 0) { notificationRepository.setNotificationSyncState(any()) }
        coVerify(exactly = 0) { notificationRepository.unsubscribeFromTopic() }
    }
}
