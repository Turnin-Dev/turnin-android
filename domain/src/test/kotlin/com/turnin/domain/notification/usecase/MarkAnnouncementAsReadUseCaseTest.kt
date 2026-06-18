package com.turnin.domain.notification.usecase

import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.AnnouncementId
import com.turnin.domain.notification.error.NotificationErrorType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkAnnouncementAsReadUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mockk()
    private val usecase = MarkAnnouncementAsReadUseCase(announcementRepository)

    @Test
    fun `공지 읽음 처리를 정상적으로 수행한다`() = runTest {
        // given
        val announcementId = 1L
        coEvery {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } returns Result.Success(Unit)

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `공지 읽음 처리 실패 시 NotificationErrorType으로 변환하여 반환한다`() = runTest {
        // given
        val announcementId = 1L
        val commonError = CommonErrorType.Unexpected(null)
        coEvery {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } returns Result.Error(commonError)

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is NotificationErrorType.CommonError)
        assertEquals(commonError, (error as NotificationErrorType.CommonError).error)
    }

    @Test
    fun `예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val announcementId = 1L
        val exception = RuntimeException("Unknown error")
        coEvery {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } throws exception

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is NotificationErrorType.Unexpected)
    }
}
