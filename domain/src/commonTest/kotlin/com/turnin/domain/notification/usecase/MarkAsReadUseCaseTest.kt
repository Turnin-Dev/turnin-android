package com.turnin.domain.notification.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.notification.repository.NotificationRepository
import com.turnin.domain.notification.error.NotificationErrorType
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MarkAsReadUseCaseTest {
    private val notificationRepository: NotificationRepository = mock()
    private val usecase = MarkAsReadUseCase(notificationRepository)

    @Test
    fun `알림 읽음 처리를 정상적으로 수행한다`() = runTest {
        // given
        everySuspend { notificationRepository.markAsRead(any()) } returns Result.Success(Unit)

        // when
        val result = usecase(notificationId = TEST_NOTIFICATION_ID)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `알림 읽음 처리 실패 시 정의된 에러를 반환한다`() = runTest {
        // given
        everySuspend {
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
