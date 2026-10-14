package com.turnin.domain.notification.usecase

import com.turnin.core.domain.announcement.model.Announcement
import com.turnin.core.domain.announcement.model.AnnouncementAudience
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.AnnouncementId
import com.turnin.domain.notification.error.NotificationErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAnnouncementsUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mockk()
    private val usecase = GetAnnouncementsUseCase(announcementRepository)

    @Test
    fun `공지 목록을 정상적으로 조회한다`() = runTest {
        // given
        val announcements = listOf(TestAnnouncement)
        every { announcementRepository.getAnnouncements() } returns flowOf(Result.Success(announcements))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(announcements, (result as Result.Success).data)
    }

    @Test
    fun `공지 목록 조회 실패 시 NotificationErrorType으로 변환하여 반환한다`() = runTest {
        // given
        val commonError = CommonErrorType.Unexpected(null)
        every { announcementRepository.getAnnouncements() } returns flowOf(Result.Error(commonError))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue(error is NotificationErrorType.CommonError)
        assertEquals(commonError, (error as NotificationErrorType.CommonError).error)
    }

    companion object {
        private val TestAnnouncement = Announcement(
            id = AnnouncementId(1L),
            title = "테스트 공지",
            content = "내용",
            targetAudience = AnnouncementAudience.ALL,
            isRead = false,
            createdAt = 1715820000000,
        )
    }
}
