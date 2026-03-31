package com.peekr.presentation.notification.viewmodel

import androidx.paging.PagingData
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.model.NotificationType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.notification.model.Notification
import com.peekr.core.presentation.MainDispatcherRule
import com.peekr.domain.notification.error.NotificationErrorType
import com.peekr.domain.notification.usecase.NotificationUseCases
import com.peekr.presentation.notification.model.toUiModel
import com.peekr.presentation.util.MockLog
import com.peekr.presentation.util.collectDataForTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val usecases: NotificationUseCases = mockk()
    private lateinit var viewModel: NotificationViewModel

    @Before
    fun setUp() {
        MockLog.mock()
        every { usecases.getNotifications() } returns TestPagingDataFlow
        viewModel = NotificationViewModel(usecases)
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    @Test
    fun `알림 목록 초기 페이징 데이터 로드 성공 테스트`() = runTest {
        // given
        val expectedPagingData = TestPagingDataFlow.first()
        val expectedList = expectedPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        ).map { it.toUiModel() }

        // when
        val actualPagingData = viewModel.notificationsPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        assertEquals(expectedList.size, actualList.size)
        assertEquals(expectedList, actualList)
    }

    @Test
    fun `알림 목록 페이지네이션 과정에서 예외 발생 시 빈 페이징 데이터를 반환한다`() = runTest {
        // given
        every { usecases.getNotifications() } returns flow {
            throw Exception("Test exception")
        }
        viewModel = NotificationViewModel(usecases)

        // when
        val pagingData = viewModel.notificationsPagingData.first()
        val actualList = pagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        assertEquals(0, actualList.size)
    }

    @Test
    fun `읽음 처리 시 해당 알림이 읽음 상태로 반영된다`() = runTest {
        // given
        coEvery { usecases.markAsRead(any()) } returns Result.Success(Unit)

        // when
        viewModel.onNotificationClick(
            notificationId = TEST_NOTIFICATION_ID,
            deepLink = TEST_DEEP_LINK,
        )
        advanceUntilIdle()

        val actualPagingData = viewModel.notificationsPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )

        // then
        val target = actualList.find { it.id == TEST_NOTIFICATION_ID }
        assertTrue(target?.isRead == true)
    }

    @Test
    fun `읽음 처리 실패 시 에러를 전파하지 않는다`() = runTest {
        // given
        coEvery {
            usecases.markAsRead(any())
        } returns Result.Error(NotificationErrorType.CommonError(CommonErrorType.Unexpected(null)))

        // when
        viewModel.onNotificationClick(
            notificationId = TEST_NOTIFICATION_ID,
            deepLink = TEST_DEEP_LINK,
        )
        advanceUntilIdle()

        // then — API 실패와 무관하게 낙관적 업데이트가 유지되어야 함
        val actualPagingData = viewModel.notificationsPagingData.first()
        val actualList = actualPagingData.collectDataForTest(
            dispatcherRule.testDispatcher,
            dispatcherRule.testDispatcher,
        )
        val target = actualList.find { it.id == TEST_NOTIFICATION_ID }
        assertTrue(target?.isRead ?: error("TEST_NOTIFICATION_ID 항목이 목록에 없습니다"))
    }

    @Test
    fun `알림 클릭 시 딥링크 이동 이벤트가 발생한다`() = runTest {
        // given
        coEvery { usecases.markAsRead(any()) } returns Result.Success(Unit)

        // when
        val navEventJob = async { viewModel.navigateToNotificationDetail.first() }
        viewModel.onNotificationClick(
            notificationId = TEST_NOTIFICATION_ID,
            deepLink = TEST_DEEP_LINK,
        )
        advanceUntilIdle()

        // then
        assertEquals(TEST_DEEP_LINK, navEventJob.await())
    }

    companion object {
        private const val TEST_LIST_SIZE = 10
        private const val TEST_NOTIFICATION_ID = 1L
        private const val TEST_DEEP_LINK = "peekr://profile/1"
        private val TestPagingDataFlow = flowOf(
            PagingData.from(
                List(TEST_LIST_SIZE) {
                    val id = it + 1L
                    Notification(
                        id = NotificationId(id),
                        userId = UserId(1L),
                        notiType = NotificationType.FRIEND_REQUEST,
                        isRead = false,
                        title = "title$id",
                        message = "message$id",
                        imageUrl = null,
                        isBroadcast = false,
                        refId = id,
                        refType = "type$id",
                        createdAt = 1000L,
                    )
                },
            ),
        )
    }
}
