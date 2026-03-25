package com.peekr.data.notification.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.NotificationId
import com.peekr.data.notification.datasource.NotificationNetworkDataSource
import com.peekr.data.notification.dto.FcmTokenResponse
import com.peekr.data.notification.dto.NotificationCursorPageResponse
import com.peekr.data.notification.dto.NotificationResponse
import com.peekr.data.util.MockLog
import com.peekr.domain.notification.error.NotificationErrorType
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.collections.emptyList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationRepositoryImplTest {
    private val dataSource: NotificationNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = NotificationRepositoryImpl(dataSource, dispatcher)

    @Before
    fun setUp() {
        coEvery { dataSource.registerFcmToken(any()) } returns NetworkResult.Success(TestFcmTokenResponse)
        coEvery { dataSource.getNotifications(any(), any()) } returns NetworkResult.Success(TestNotificationCursorPageResponse)
        coEvery { dataSource.markAsRead(any()) } returns NetworkResult.Success(Unit)
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
        assertEquals(
            NotificationErrorType.CommonError(expectedError.toCommonErrorType()),
            error.error,
        )
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
        val result = repository.markAsRead(TestNotificationId).last()

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
        val result = repository.markAsRead(TestNotificationId).last()

        // then
        val error = result as Result.Error
        assertEquals(
            NotificationErrorType.CommonError(expectedError.toCommonErrorType()),
            error.error,
        )
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
