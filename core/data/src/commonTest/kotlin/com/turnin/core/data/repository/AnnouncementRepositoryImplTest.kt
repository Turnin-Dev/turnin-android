package com.turnin.core.data.repository

import com.turnin.core.data.source.network.datasource.AnnouncementNetworkDataSource
import com.turnin.core.data.source.network.dto.announcement.response.AnnouncementResponse
import com.turnin.core.data.source.network.dto.announcement.response.toDomainModel
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.announcement.repository.AnnouncementRepository
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.AnnouncementId
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        val result = repository.getAnnouncements().last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            mockResponses.map { it.toDomainModel() },
            (result as Result.Success).data,
        )
    }

    @Test
    fun `getAnnouncements 실패 테스트`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.HttpError(500)
        val expectedMessage = "Server Error"
        everySuspend { dataSource.getAnnouncements() } returns NetworkResult.Error(
            error = expectedError,
            message = expectedMessage,
        )

        // when
        val result = repository.getAnnouncements().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError.toCommonErrorType(), (result as Result.Error).error)
        assertEquals(expectedMessage, result.message)
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
        val expectedError = NetworkErrorType.Network.HttpError(400)
        everySuspend { dataSource.markAsRead(announcementId) } returns NetworkResult.Error(
            error = expectedError,
            message = "Bad Request",
        )

        // when
        val result = repository.markAsRead(announcementId)

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError.toCommonErrorType(), (result as Result.Error).error)
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
