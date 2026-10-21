package com.turnin.shared.domain.feature.announcement.usecase

import com.turnin.shared.domain.core.error.NetworkErrorType
import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.domain.feature.announcement.model.Announcement
import com.turnin.shared.domain.feature.announcement.model.AnnouncementAudience
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest

class GetAnnouncementsUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mock()
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
        assertEquals(announcements, result.data)
    }

    @Test
    fun `공지 목록 조회 실패 시 AnnouncementErrorType으로 변환하여 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.Unauthorized
        every { announcementRepository.getAnnouncements() } returns flowOf(Result.Error(networkError))

        // when
        val result = usecase().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(AnnouncementErrorType.Network(networkError), result.error)
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
