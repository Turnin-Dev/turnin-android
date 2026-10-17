package com.turnin.shared.data.feature.announcement.repository

import com.turnin.shared.data.core.network.NetworkResult
import com.turnin.shared.data.feature.announcement.datasource.AnnouncementNetworkDataSource
import com.turnin.shared.data.feature.announcement.dto.AnnouncementResponse
import com.turnin.shared.data.feature.announcement.dto.toDomainModel
import com.turnin.shared.domain.core.error.NetworkErrorType
import com.turnin.shared.domain.core.result.Result
import com.turnin.shared.domain.feature.announcement.model.AnnouncementId
import com.turnin.shared.domain.feature.announcement.repository.AnnouncementRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class AnnouncementRepositoryImplTest {
    private val dataSource: AnnouncementNetworkDataSource = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: AnnouncementRepository =
        AnnouncementRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `getAnnouncements 성공 테스트`() = runTest {
        // given
        val mockResponses = listOf(mockAnnouncementResponse)
        everySuspend { dataSource.getAnnouncements() } returns NetworkResult.Success(mockResponses)

        // when
        val flow = repository.getAnnouncements()

        // then
        assertEquals(Result.Loading, flow.first())
        val result = flow.last()
        assertTrue(result is Result.Success)
        assertEquals(mockResponses.map { it.toDomainModel() }, result.data)
    }

    @Test
    fun `getAnnouncements 실패 테스트`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.InternalServerError
        val expectedMessage = "Server Error"
        everySuspend { dataSource.getAnnouncements() } returns NetworkResult.Error(
            error = expectedError,
            message = expectedMessage,
        )

        // when
        val result = repository.getAnnouncements().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, result.error)
        assertEquals(expectedMessage, result.message)
    }

    @Test
    fun `getAnnouncements 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("boom")
        everySuspend { dataSource.getAnnouncements() } throws exception

        // when
        val result = repository.getAnnouncements().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(NetworkErrorType.Unexpected(exception), result.error)
    }

    @Test
    fun `markAsRead 성공 테스트`() = runTest {
        // given
        val announcementId = AnnouncementId(1L)
        everySuspend { dataSource.markAsRead(announcementId) } returns NetworkResult.Success(Unit)

        // when
        val result = repository.markAsRead(announcementId)

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `markAsRead 실패 테스트`() = runTest {
        // given
        val announcementId = AnnouncementId(1L)
        val expectedError = NetworkErrorType.Network.BadRequest
        everySuspend { dataSource.markAsRead(announcementId) } returns NetworkResult.Error(
            error = expectedError,
            message = "Bad Request",
        )

        // when
        val result = repository.markAsRead(announcementId)

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError, result.error)
    }

    companion object {
        private val mockAnnouncementResponse = AnnouncementResponse(
            id = 1,
            title = "테스트 공지",
            content = "내용",
            targetAudience = "ALL",
            isRead = false,
            createdAt = 1715820000000,
        )
    }
}
