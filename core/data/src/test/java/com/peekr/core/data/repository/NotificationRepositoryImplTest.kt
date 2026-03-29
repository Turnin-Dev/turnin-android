package com.peekr.core.data.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.data.MockLog
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.network.datasource.NotificationNetworkDataSource
import com.peekr.core.data.source.network.dto.notification.response.FcmTokenResponse
import com.peekr.core.data.source.network.dto.notification.response.NotificationCursorPageResponse
import com.peekr.core.data.source.network.dto.notification.response.NotificationResponse
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.notification.model.NotificationPagingTokens
import com.peekr.core.domain.setting.model.NotificationSyncState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationRepositoryImplTest {
    private val dataSource: NotificationNetworkDataSource = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = NotificationRepositoryImpl(dataSource, dataStoreManager, dispatcher)

    @Before
    fun setUp() {
        coEvery { dataSource.registerFcmToken(any()) } returns NetworkResult.Success(TestFcmTokenResponse)
        coEvery { dataSource.getNotifications(any(), any()) } returns NetworkResult.Success(TestNotificationCursorPageResponse)
        coEvery { dataSource.markAsRead(any()) } returns NetworkResult.Success(Unit)
        coEvery { dataSource.deactivateToken(any()) } returns NetworkResult.Success(Unit)
        coEvery { dataStoreManager.saveStringData(any(), any()) } returns Unit
        coEvery { dataStoreManager.getStringData(any()) } returns flowOf("REGISTERED")
        MockLog.mock()
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    // ======================== registerFcmToken ========================

    @Test
    fun `FCM 토큰 등록 - 성공 시 Success를 반환한다`() = runTest {
        // when
        val result = repository.registerFcmToken(TEST_TOKEN)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `FCM 토큰 등록 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.registerFcmToken(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.registerFcmToken(TEST_TOKEN)

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    // ======================== getNotifications ========================

    @Test
    fun `알림 목록 조회 - 초기 호출 성공 시 도메인 모델로 변환된 데이터를 반환한다`() = runTest {
        // given
        val firstPage = createCursorPageResponse(nextCursor = null, NotificationPagingTokens.PAGE_SIZE)
        coEvery {
            dataSource.getNotifications(any(), any())
        } returns NetworkResult.Success(firstPage)

        // when
        val notifications = repository.getNotifications().asSnapshot()

        // then
        assertEquals(NotificationPagingTokens.PAGE_SIZE, notifications.size)
        assertEquals(firstPage.items.first().toDomainModel(), notifications.first())
    }

    @Test
    fun `알림 목록 조회 - 스크롤 시 다음 페이지가 로드된다`() = runTest {
        // given
        val pageSize = NotificationPagingTokens.PAGE_SIZE
        val firstPage = createCursorPageResponse(nextCursor = 20L, pageSize = pageSize, startId = 0)
        val secondPage = createCursorPageResponse(nextCursor = null, pageSize = pageSize, startId = pageSize)

        coEvery {
            dataSource.getNotifications(any(), any())
        } answers {
            val cursor = firstArg<Long?>()
            when (cursor) {
                null -> NetworkResult.Success(firstPage)
                20L -> NetworkResult.Success(secondPage)
                else -> NetworkResult.Success(
                    NotificationCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when
        val notifications = repository.getNotifications().asSnapshot {
            scrollTo(pageSize)
        }

        // then
        assertEquals(pageSize * 2, notifications.size)
        assertEquals(firstPage.items.first().toDomainModel(), notifications.first())
        assertEquals(secondPage.items.last().toDomainModel(), notifications.last())
    }

    // ======================== markAsRead ========================

    @Test
    fun `알림 읽음 처리 - 성공 테스트`() = runTest {
        // when
        val result = repository.markAsRead(TestNotificationId)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `알림 읽음 처리 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.markAsRead(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.markAsRead(TestNotificationId)

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    // ======================== deactivateFcmToken ========================

    @Test
    fun `FCM 토큰 비활성화 - 성공 시 Success를 반환한다`() = runTest {
        // when
        val result = repository.deactivateFcmToken(TEST_TOKEN)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `FCM 토큰 비활성화 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.deactivateToken(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deactivateFcmToken(TEST_TOKEN)

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    // ======================== getNotificationSyncState ========================

    @Test
    fun `getNotificationSyncState는 DataStore에 저장된 값을 반환한다`() = runTest {
        // given
        val state = NotificationSyncState.REGISTERED
        every {
            dataStoreManager.getStringData(DataStoreKey.Setting.NotificationSyncState)
        } returns flowOf(state.name)

        // when
        val result = repository.getNotificationSyncState()

        // then
        assertEquals(state, result)
    }

    @Test
    fun `getNotificationSyncState는 DataStore가 비어있을 때 null을 반환한다`() = runTest {
        // given
        every {
            dataStoreManager.getStringData(DataStoreKey.Setting.NotificationSyncState)
        } returns flowOf(null)

        // when
        val result = repository.getNotificationSyncState()

        // then
        assertNull(result)
    }

    @Test
    fun `getNotificationSyncState는 유효하지 않은 값일 때 null을 반환한다`() = runTest {
        // given
        every {
            dataStoreManager.getStringData(DataStoreKey.Setting.NotificationSyncState)
        } returns flowOf("INVALID_VALUE")

        // when
        val result = repository.getNotificationSyncState()

        // then
        assertNull(result)
    }

    // ======================== setNotificationSyncState ========================

    @Test
    fun `setNotificationSyncState 호출 시 DataStore에 동기화 상태가 저장된다`() = runTest {
        // given
        val state = NotificationSyncState.DEACTIVATED
        coEvery { dataStoreManager.saveStringData(any(), any()) } returns Unit

        // when
        repository.setNotificationSyncState(state)

        // then
        coVerify {
            dataStoreManager.saveStringData(
                key = DataStoreKey.Setting.NotificationSyncState,
                value = state.name,
            )
        }
    }

    companion object {
        private const val TEST_TOKEN = "test_fcm_token"
        private val TestNotificationId = NotificationId(1L)
        private val TestFcmTokenResponse = FcmTokenResponse(
            id = 1L,
            userId = 1L,
            token = TEST_TOKEN,
            isActive = true,
        )
        private val TestNotificationResponse = NotificationResponse(
            id = 1L,
            notiType = "FRIEND_REQUEST",
            title = "친구 요청",
            message = "홍길동님이 친구 요청을 보냈어요.",
            imageUrl = null,
            isRead = false,
            isBroadcast = false,
            refId = 2L,
            refType = "USER",
            createdAt = 1716000000L,
        )
        private val TestNotificationCursorPageResponse = NotificationCursorPageResponse(
            items = listOf(TestNotificationResponse),
            nextCursor = TestNotificationResponse.id,
        )

        private fun createCursorPageResponse(
            nextCursor: Long?,
            pageSize: Int,
            startId: Int = 0,
        ): NotificationCursorPageResponse =
            NotificationCursorPageResponse(
                items = List(pageSize) {
                    NotificationResponse(
                        id = (startId + it).toLong(),
                        notiType = "FRIEND_REQUEST",
                        title = "친구 요청 ${startId + it}",
                        message = "message ${startId + it}",
                        imageUrl = null,
                        isRead = false,
                        isBroadcast = false,
                        refId = (startId + it).toLong(),
                        refType = "USER",
                        createdAt = 1716000000L,
                    )
                },
                nextCursor = nextCursor,
            )
    }
}
