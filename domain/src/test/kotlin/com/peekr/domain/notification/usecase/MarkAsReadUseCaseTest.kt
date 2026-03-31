package com.peekr.domain.notification.usecase

import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.notification.repository.NotificationRepository
import com.peekr.domain.notification.error.NotificationErrorType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkAsReadUseCaseTest {
    private val notificationRepository: NotificationRepository = mockk()
    private val usecase = MarkAsReadUseCase(notificationRepository)

    @Test
    fun `알림 읽음 처리를 정상적으로 수행한다`() = runTest {
        // given
        coEvery { notificationRepository.markAsRead(any()) } returns Result.Success(Unit)

        // when
        val result = usecase(notificationId = TEST_NOTIFICATION_ID)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `알림 읽음 처리 실패 시 정의된 에러를 반환한다`() = runTest {
        // given
        coEvery {
            notificationRepository.markAsRead(any())
        } returns Result.Error(CommonErrorType.Unexpected(null))

        // when
        val result = usecase(notificationId = TEST_NOTIFICATION_ID)

        // then
        val error = result as Result.Error
        assertTrue(error.error is NotificationErrorType.CommonError)
    }

    companion object {
        private const val TEST_NOTIFICATION_ID = 1L
    }
}
