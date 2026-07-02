package com.turnin.presentation.notification.viewmodel

import androidx.paging.PagingData
import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.announcement.model.AnnouncementAudience
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.AnnouncementId
import com.turnin.core.domain.model.NotificationId
import com.turnin.core.domain.model.NotificationType
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.notification.model.Notification
import com.turnin.core.presentation.MainDispatcherRule
import com.turnin.domain.notification.error.NotificationErrorType
import com.turnin.domain.notification.usecase.NotificationUseCases
import com.turnin.presentation.notification.model.toUiModel
import com.turnin.presentation.util.MockLog
import com.turnin.presentation.util.collectDataForTest
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertNull
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
        every { usecases.getNotifications() } answers { createTestPagingDataFlow() }
        every { usecases.getAnnouncements() } returns flowOf(Result.Success(emptyList()))
        viewModel = NotificationViewModel(usecases)
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    @Test
    fun `알림 목록 초기 페이징 데이터 로드 성공 테스트`() = runTest {
        // given
        val expectedList = testNotifications.map { it.toUiModel() }

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
            currentIsRead = false,
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
            currentIsRead = false,
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
            currentIsRead = false,
        )
        advanceUntilIdle()

        // then
        assertEquals(TEST_DEEP_LINK, navEventJob.await())
    }

    @Test
    fun `이미 읽은 알림은 네트워크 호출이 발생하지 않는다`() = runTest {
        // given
        coEvery { usecases.markAsRead(any()) } returns Result.Success(Unit)

        // when
        viewModel.onNotificationClick(
            notificationId = TEST_NOTIFICATION_ID,
            deepLink = TEST_DEEP_LINK,
            currentIsRead = true,
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
        coVerify(exactly = 0) { usecases.markAsRead(any()) }
    }

    // ------------------------------ 공지 알림 ------------------------------

    @Test
    fun `공지 목록 조회 성공 시 UI 상태가 정상적으로 업데이트된다`() = runTest {
        // given
        val announcements = listOf(testAnnouncement)
        every { usecases.getAnnouncements() } returns flowOf(Result.Success(announcements))

        // when
        viewModel.getAnnouncements()
        advanceUntilIdle()

        // then
        val state = viewModel.announcementUiState.value
        assertEquals(false, state.loading)
        assertNull(state.error)
        assertEquals(1, state.announcements.size)
        assertEquals(testAnnouncement.title, state.announcements[0].title)
    }

    @Test
    fun `공지 목록 조회 실패 시 에러 상태가 반영된다`() = runTest {
        // given
        val errorType = NotificationErrorType.CommonError(CommonErrorType.Unexpected(null))
        every { usecases.getAnnouncements() } returns flowOf(Result.Error(errorType))

        // when
        viewModel.getAnnouncements()
        advanceUntilIdle()

        // then
        val state = viewModel.announcementUiState.value
        assertEquals(false, state.loading)
        assertTrue(state.error != null)
    }

    @Test
    fun `공지 읽음 처리 시 해당 공지가 읽음 상태로 즉시 반영된다`() = runTest {
        // given
        val announcements = listOf(testAnnouncement.copy(isRead = false))
        every { usecases.getAnnouncements() } returns flowOf(Result.Success(announcements))
        coEvery { usecases.markAnnouncementAsRead(any()) } returns Result.Success(Unit)

        viewModel.getAnnouncements()
        advanceUntilIdle()

        // when
        viewModel.markAnnouncementAsRead(
            announcementId = 1L,
            currentIsRead = false,
        )
        advanceUntilIdle()

        // then
        val state = viewModel.announcementUiState.value
        val target = state.announcements.find { it.id == 1L }
        assertTrue(target?.isRead == true)
        coVerify(exactly = 1) { usecases.markAnnouncementAsRead(1L) }
    }

    @Test
    fun `이미 읽은 공지는 네트워크 호출이 발생하지 않는다`() = runTest {
        // given
        val announcements = listOf(testAnnouncement.copy(isRead = true))
        every { usecases.getAnnouncements() } returns flowOf(Result.Success(announcements))
        coEvery { usecases.markAnnouncementAsRead(any()) } returns Result.Success(Unit)

        viewModel.getAnnouncements()
        advanceUntilIdle()

        // when
        viewModel.markAnnouncementAsRead(
            announcementId = 1L,
            currentIsRead = true,
        )
        advanceUntilIdle()

        // then
        coVerify(exactly = 0) { usecases.markAnnouncementAsRead(any()) }
    }

    companion object {
        private const val TEST_LIST_SIZE = 10
        private const val TEST_NOTIFICATION_ID = 1L
        private const val TEST_DEEP_LINK = "turnin://profile/1"

        private val testNotifications = List(TEST_LIST_SIZE) {
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
        }

        private val testAnnouncement = Announcement(
            id = AnnouncementId(1L),
            title = "테스트 공지",
            content = "내용",
            targetAudience = AnnouncementAudience.ALL,
            isRead = false,
            createdAt = 1715820000000,
        )

        private fun createTestPagingDataFlow() = flowOf(
            PagingData.from(testNotifications),
        )
    }
}
