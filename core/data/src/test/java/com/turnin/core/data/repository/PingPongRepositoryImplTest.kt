package com.turnin.core.data.repository

import androidx.paging.testing.asSnapshot
import com.turnin.core.data.MockLog
import com.turnin.core.data.source.network.datasource.PingPongNetworkDataSource
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongAnswerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongCursorPageResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongDetailResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongQuestionerResponse
import com.turnin.core.data.source.network.dto.pingPong.response.PingPongResponse
import com.turnin.core.data.source.network.dto.pingPong.response.toDomainModel
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPongAnswerContent
import com.turnin.core.domain.pingPong.model.PingPongQuestionContent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * PingPongRepository + 페이징 테스트가 포함
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PingPongRepositoryImplTest {
    private val dataSource: PingPongNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = PingPongRepositoryImpl(dataSource, dispatcher)

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    // ------------------------------ 목록 조회 ------------------------------

    @Test
    fun `핑퐁 목록 조회 - 다음 커서로 이어서 조회한 결과를 도메인 모델로 변환해 반환한다`() = runTest {
        // given: 2페이지 테스트 데이터 설정
        val page1 = createCursorPageResponse(startId = 1L, nextCursor = TEST_NEXT_CURSOR)
        val page2 = createCursorPageResponse(startId = 100L, nextCursor = null)
        coEvery {
            dataSource.getPingPongs(TEST_USER_KEYWORD_ID.value, any(), any())
        } answers {
            when (secondArg<Long?>()) {
                null -> NetworkResult.Success(page1)
                TEST_NEXT_CURSOR -> NetworkResult.Success(page2)
                else -> NetworkResult.Success(PingPongCursorPageResponse(emptyList(), null))
            }
        }

        // when
        val pingPongs = repository.getPingPongs(TEST_USER_KEYWORD_ID).asSnapshot()

        // then
        assertEquals(page1.items.size + page2.items.size, pingPongs.size)
        assertEquals(page1.items.first().toDomainModel(), pingPongs.first())
        assertEquals(page2.items.last().toDomainModel(), pingPongs.last())
    }

    @Test
    fun `핑퐁 목록 조회 - 답변이 없는 항목은 답변을 null로 변환한다`() = runTest {
        // given
        val page = PingPongCursorPageResponse(
            items = listOf(createDetailResponse(id = 1L, answer = null)),
            nextCursor = null,
        )
        coEvery { dataSource.getPingPongs(any(), any(), any()) } returns NetworkResult.Success(page)

        // when
        val pingPongs = repository.getPingPongs(TEST_USER_KEYWORD_ID).asSnapshot()

        // then
        assertNull(pingPongs.single().answer)
    }

    // ------------------------------ 질문 작성 ------------------------------

    @Test
    fun `질문 작성 - 성공 시 생성된 핑퐁을 도메인 모델로 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.createPingPong(any(), any())
        } returns NetworkResult.Success(TestPingPongResponse)

        // when
        val result = repository.createPingPong(TEST_USER_KEYWORD_ID, TestQuestion).last()

        // then
        val success = result as Result.Success
        assertEquals(TestPingPongResponse.toDomainModel(), success.data)
    }

    @Test
    fun `질문 작성 - VO의 원시 값으로 데이터소스를 호출한다`() = runTest {
        // given
        coEvery {
            dataSource.createPingPong(any(), any())
        } returns NetworkResult.Success(TestPingPongResponse)

        // when
        repository.createPingPong(TEST_USER_KEYWORD_ID, TestQuestion).last()

        // then
        coVerify(exactly = 1) {
            dataSource.createPingPong(TEST_USER_KEYWORD_ID.value, TestQuestion.value)
        }
    }

    @Test
    fun `질문 작성 - Loading을 먼저 방출한다`() = runTest {
        // given
        coEvery {
            dataSource.createPingPong(any(), any())
        } returns NetworkResult.Success(TestPingPongResponse)

        // when
        val first = repository.createPingPong(TEST_USER_KEYWORD_ID, TestQuestion).first()

        // then
        assertEquals(Result.Loading, first)
    }

    @Test
    fun `질문 작성 - 네트워크 에러 시 공통 에러 타입으로 변환해 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(429)
        coEvery {
            dataSource.createPingPong(any(), any())
        } returns NetworkResult.Error(networkError, message = "too many requests")

        // when
        val result = repository.createPingPong(TEST_USER_KEYWORD_ID, TestQuestion).last()

        // then
        val error = result as Result.Error
        assertEquals(networkError.toCommonErrorType(), error.error)
        assertEquals("too many requests", error.message)
    }

    @Test
    fun `질문 작성 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        coEvery { dataSource.createPingPong(any(), any()) } throws exception

        // when
        val result = repository.createPingPong(TEST_USER_KEYWORD_ID, TestQuestion).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Unexpected(exception), error.error)
    }

    // ------------------------------ 답변 작성 ------------------------------

    @Test
    fun `답변 작성 - 성공 시 생성된 답변을 도메인 모델로 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.createAnswer(any(), any())
        } returns NetworkResult.Success(TestAnswerResponse)

        // when
        val result = repository.createAnswer(TEST_PING_PONG_ID, TestAnswer).last()

        // then
        val success = result as Result.Success
        assertEquals(TestAnswerResponse.toDomainModel(), success.data)
    }

    @Test
    fun `답변 작성 - VO의 원시 값으로 데이터소스를 호출한다`() = runTest {
        // given
        coEvery {
            dataSource.createAnswer(any(), any())
        } returns NetworkResult.Success(TestAnswerResponse)

        // when
        repository.createAnswer(TEST_PING_PONG_ID, TestAnswer).last()

        // then
        coVerify(exactly = 1) { dataSource.createAnswer(TEST_PING_PONG_ID.value, TestAnswer.value) }
    }

    @Test
    fun `답변 작성 - 네트워크 에러 시 공통 에러 타입으로 변환해 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(409)
        coEvery {
            dataSource.createAnswer(any(), any())
        } returns NetworkResult.Error(networkError)

        // when
        val result = repository.createAnswer(TEST_PING_PONG_ID, TestAnswer).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.Conflict, error.error)
    }

    @Test
    fun `답변 작성 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        coEvery { dataSource.createAnswer(any(), any()) } throws exception

        // when
        val result = repository.createAnswer(TEST_PING_PONG_ID, TestAnswer).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Unexpected(exception), error.error)
    }

    // ------------------------------ 질문 삭제 ------------------------------

    @Test
    fun `질문 삭제 - 성공 시 Success를 반환한다`() = runTest {
        // given
        coEvery { dataSource.deletePingPong(TEST_PING_PONG_ID.value) } returns NetworkResult.Success(Unit)

        // when
        val result = repository.deletePingPong(TEST_PING_PONG_ID).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `질문 삭제 - 네트워크 에러 시 공통 에러 타입으로 변환해 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(403)
        coEvery { dataSource.deletePingPong(any()) } returns NetworkResult.Error(networkError)

        // when
        val result = repository.deletePingPong(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.Forbidden, error.error)
    }

    @Test
    fun `질문 삭제 - 서버 에러 코드를 계약된 에러 코드로 변환해 함께 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.deletePingPong(any())
        } returns NetworkResult.Error(
            error = NetworkErrorType.Network.HttpError(403),
            code = "PP007",
            status = 403,
        )

        // when
        val result = repository.deletePingPong(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(ContractErrorCode.PingPong.PP007, error.code)
    }

    @Test
    fun `질문 삭제 - 서버 에러 코드가 없으면 Unexpected 에러 코드를 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.deletePingPong(any())
        } returns NetworkResult.Error(NetworkErrorType.Exception.TimeOut)

        // when
        val result = repository.deletePingPong(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(ContractErrorCode.Unexpected, error.code)
    }

    @Test
    fun `질문 삭제 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        coEvery { dataSource.deletePingPong(any()) } throws exception

        // when
        val result = repository.deletePingPong(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Unexpected(exception), error.error)
    }

    // ------------------------------ 답변 삭제 ------------------------------

    @Test
    fun `답변 삭제 - 성공 시 Success를 반환한다`() = runTest {
        // given
        coEvery {
            dataSource.deletePingPongAnswer(TEST_PING_PONG_ID.value)
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository.deletePingPongAnswer(TEST_PING_PONG_ID).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `답변 삭제 - 네트워크 에러 시 공통 에러 타입으로 변환해 반환한다`() = runTest {
        // given
        val networkError = NetworkErrorType.Network.HttpError(404)
        coEvery { dataSource.deletePingPongAnswer(any()) } returns NetworkResult.Error(networkError)

        // when
        val result = repository.deletePingPongAnswer(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Network.NotFound, error.error)
    }

    @Test
    fun `답변 삭제 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        coEvery { dataSource.deletePingPongAnswer(any()) } throws exception

        // when
        val result = repository.deletePingPongAnswer(TEST_PING_PONG_ID).last()

        // then
        val error = result as Result.Error
        assertEquals(CommonErrorType.Unexpected(exception), error.error)
    }

    companion object {
        private val TEST_USER_KEYWORD_ID = UserKeywordId(10L)
        private val TEST_PING_PONG_ID = PingPongId(1L)
        private const val TEST_NEXT_CURSOR = 50L
        private const val TEST_ITEMS_PER_PAGE = 5

        private val TestQuestion = PingPongQuestionContent.create("질문")
        private val TestAnswer = PingPongAnswerContent("답변")

        private val TestPingPongResponse = createPingPongResponse(id = TEST_PING_PONG_ID.value)
        private val TestAnswerResponse = createAnswerResponse(pingPongId = TEST_PING_PONG_ID.value)

        private fun createPingPongResponse(id: Long) = PingPongResponse(
            id = id,
            userKeywordId = TEST_USER_KEYWORD_ID.value,
            questionerId = 2L,
            question = "질문 $id?",
            createdAt = 1_000L,
            updatedAt = 1_000L,
        )

        private fun createAnswerResponse(pingPongId: Long) = PingPongAnswerResponse(
            id = pingPongId + 1_000L,
            pingPongId = pingPongId,
            answer = "답변 $pingPongId",
            createdAt = 2_000L,
            updatedAt = 2_000L,
        )

        private fun createDetailResponse(
            id: Long,
            answer: PingPongAnswerResponse? = createAnswerResponse(id),
        ) = PingPongDetailResponse(
            pingPong = createPingPongResponse(id),
            questioner = PingPongQuestionerResponse(
                userId = 2L,
                userName = "질문자",
                profileImageUrl = null,
            ),
            answer = answer,
        )

        private fun createCursorPageResponse(
            startId: Long,
            nextCursor: Long?,
        ) = PingPongCursorPageResponse(
            items = (startId until startId + TEST_ITEMS_PER_PAGE).map { createDetailResponse(it) },
            nextCursor = nextCursor,
        )
    }
}
