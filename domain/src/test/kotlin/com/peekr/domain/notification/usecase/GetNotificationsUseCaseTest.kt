package com.peekr.domain.notification.usecase

import androidx.paging.PagingData
import com.peekr.core.domain.model.NotificationId
import com.peekr.core.domain.model.NotificationType
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.notification.model.Notification
import com.peekr.core.domain.notification.repository.NotificationRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Test

class GetNotificationsUseCaseTest {
    private val notificationRepository: NotificationRepository = mockk()
    private val usecase = GetNotificationsUseCase(notificationRepository)

    @Test
    fun `알림 목록을 정상적으로 조회한다`() = runTest {
        // given
        val pagingData = PagingData.from(listOf(TestNotification))
        every { notificationRepository.getNotifications() } returns flowOf(pagingData)

        // when
        val result = usecase().first()

        // then
        assertNotNull(result)
    }

    companion object {
        private val TestNotification = Notification(
            id = NotificationId(1L),
            userId = UserId(1L),
            notiType = NotificationType.FRIEND_REQUEST,
            title = "친구 요청",
            message = "홍길동님이 친구 요청을 보냈어요.",
            imageUrl = null,
            isRead = false,
            isBroadcast = false,
            refId = 2L,
            refType = "USER",
            createdAt = 1716000000L,
        )
    }
}
