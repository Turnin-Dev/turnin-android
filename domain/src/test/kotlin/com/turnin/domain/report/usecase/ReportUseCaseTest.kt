package com.turnin.domain.report.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.report.model.ReportReasonId
import com.turnin.core.domain.report.repository.ReportRepository
import com.turnin.core.domain.user.repository.UserRepository
import com.turnin.domain.report.error.ReportErrorType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReportUseCaseTest {
    private val userRepository: UserRepository = mockk()
    private val reportRepository: ReportRepository = mockk()
    private val usecase = ReportUseCase(userRepository, reportRepository)

    @Before
    fun setUp() {
        coEvery { userRepository.getMyUserId() } returns TestUserId
    }

    @Test
    fun `신고를 정상적으로 수행한다`() = runTest {
        // given
        every {
            reportRepository.createReport(any())
        } returns flowOf(Result.Success(Unit))

        // when
        val result = usecase(
            reportedId = TEST_REPORTED_ID,
            reportedUserKeywordId = null,
            reasonId = TestReportReasonId,
            customReason = null,
        ).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `신고 대상이 모두 비어있으면 정의된 에러를 반환한다`() = runTest {
        // when
        val result = usecase(
            reportedId = null,
            reportedUserKeywordId = null,
            reasonId = TestReportReasonId,
            customReason = null,
        ).last()

        // then
        val error = result as Result.Error
        assertEquals(ReportErrorType.MissingReportTarget, error.error)
    }

    @Test
    fun `중복 신고 시 정의된 에러를 반환한다`() = runTest {
        // given
        every {
            reportRepository.createReport(any())
        } returns flowOf(Result.Error(CommonErrorType.Network.Conflict))

        // when
        val result = usecase(
            reportedId = TEST_REPORTED_ID,
            reportedUserKeywordId = null,
            reasonId = TestReportReasonId,
            customReason = null,
        ).last()

        // then
        val error = result as Result.Error
        assertEquals(ReportErrorType.AlreadyReported, error.error)
    }

    @Test
    fun `사용자 ID를 가져오지 못하는 경우 에러를 반환한다`() = runTest {
        // given
        coEvery { userRepository.getMyUserId() } returns null

        // when
        val result = usecase(
            reportedId = TEST_REPORTED_ID,
            reportedUserKeywordId = null,
            reasonId = TestReportReasonId,
            customReason = null,
        ).last()

        // then
        val error = result as Result.Error
        assertEquals(ReportErrorType.UserIdNotFound, error.error)
    }

    companion object {
        private val TestUserId = UserId(10L)
        private const val TEST_REPORTED_ID = 1L
        private val TestReportReasonId = ReportReasonId(1L)
    }
}
