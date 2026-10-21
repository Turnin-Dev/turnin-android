package com.turnin.shared.domain.feature.announcement.usecase

import com.turnin.shared.domain.core.error.NetworkErrorType
import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.error.AnnouncementErrorType
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MarkAnnouncementAsReadUseCaseTest {
    private val announcementRepository: AnnouncementRepository = mock()
    private val usecase = MarkAnnouncementAsReadUseCase(announcementRepository)

    @Test
    fun `공지 읽음 처리를 정상적으로 수행한다`() = runTest {
        // given
        val announcementId = 1L
        everySuspend {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } returns Result.Success(Unit)

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `공지 읽음 처리 실패 시 AnnouncementErrorType으로 변환하여 반환한다`() = runTest {
        // given
        val announcementId = 1L
        val networkError = NetworkErrorType.Exception.IO
        everySuspend {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } returns Result.Error(networkError)

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Error)
        assertEquals(AnnouncementErrorType.Network(networkError), result.error)
    }

    @Test
    fun `예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val announcementId = 1L
        val exception = RuntimeException("Unknown error")
        everySuspend {
            announcementRepository.markAsRead(AnnouncementId(announcementId))
        } throws exception

        // when
        val result = usecase(announcementId)

        // then
        assertTrue(result is Result.Error)
        assertEquals(AnnouncementErrorType.Unexpected(exception), result.error)
    }
}
