package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.domain.pingPong.model.PingPong
import com.turnin.core.domain.pingPong.model.PingPongQuestionContent
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreatePingPongUseCaseTest {
    private val pingPongRepository: PingPongRepository = mockk()
    private val useCase = CreatePingPongUseCase(pingPongRepository)

    @Test
    fun `질문 작성 - 질문 형식 규칙을 적용한 질문으로 등록한다`() = runTest {
        // given
        every { pingPongRepository.createPingPong(any(), any()) } returns flowOf(Result.Success(TestPingPong))

        // when
        useCase(TEST_USER_KEYWORD_ID, "  질문  ").last()

        // then
        verify(exactly = 1) {
            pingPongRepository.createPingPong(
                UserKeywordId(TEST_USER_KEYWORD_ID),
                PingPongQuestionContent.create("질문"),
            )
        }
    }

    @Test
    fun `질문 작성 - 성공 시 등록된 핑퐁을 반환한다`() = runTest {
        // given
        every { pingPongRepository.createPingPong(any(), any()) } returns flowOf(Result.Success(TestPingPong))

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last()

        // then
        assertEquals(Result.Success(TestPingPong), result)
    }

    @Test
    fun `질문 작성 - 물음표만 입력하면 저장소를 호출하지 않고 InvalidQuestion 에러를 반환한다`() = runTest {
        // when
        val result = useCase(TEST_USER_KEYWORD_ID, " ? ").last()

        // then
        assertEquals(PingPongErrorType.InvalidQuestion, (result as Result.Error).error)
        verify(exactly = 0) { pingPongRepository.createPingPong(any(), any()) }
    }

    @Test
    fun `질문 작성 - 최대 글자 수를 초과하면 InvalidQuestion 에러를 반환한다`() = runTest {
        // given: 붙는 물음표까지 포함하면 최대 글자 수를 초과하는 질문
        val question = "가".repeat(PingPongQuestionContent.MAX_LENGTH)

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, question).last()

        // then
        assertEquals(PingPongErrorType.InvalidQuestion, (result as Result.Error).error)
    }

    @Test
    fun `질문 작성 - PP001 코드는 UserKeywordNotFound 에러로 변환한다`() = runTest {
        // given
        givenRepositoryError(CommonErrorType.Network.NotFound, ContractErrorCode.PingPong.PP001)

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last()

        // then
        assertEquals(PingPongErrorType.UserKeywordNotFound, (result as Result.Error).error)
    }

    @Test
    fun `질문 작성 - PP002 코드는 CannotQuestionOwnUserKeyword 에러로 변환한다`() = runTest {
        // given
        givenRepositoryError(CommonErrorType.Network.BadRequest, ContractErrorCode.PingPong.PP002)

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last()

        // then
        assertEquals(PingPongErrorType.CannotQuestionOwnUserKeyword, (result as Result.Error).error)
    }

    @Test
    fun `질문 작성 - 요청 횟수 제한 초과(TooManyRequests)는 TooManyQuestions 에러로 변환한다`() = runTest {
        // given
        givenRepositoryError(CommonErrorType.Network.TooManyRequests, ContractErrorCode.Unexpected)

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last()

        // then
        assertEquals(PingPongErrorType.TooManyQuestions, (result as Result.Error).error)
    }

    @Test
    fun `질문 작성 - 구분되지 않는 에러는 CommonError로 감싸 서버 에러 코드와 함께 반환한다`() = runTest {
        // given
        givenRepositoryError(CommonErrorType.Network.InternalServerError, ContractErrorCode.Unexpected)

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last() as Result.Error

        // then
        assertEquals(PingPongErrorType.CommonError(CommonErrorType.Network.InternalServerError), result.error)
        assertEquals(ContractErrorCode.Unexpected, result.code)
    }

    @Test
    fun `질문 작성 - 예외 발생 시 Unexpected 에러를 반환한다`() = runTest {
        // given
        val exception = IllegalStateException("error!")
        every { pingPongRepository.createPingPong(any(), any()) } returns flow { throw exception }

        // when
        val result = useCase(TEST_USER_KEYWORD_ID, "질문").last()

        // then
        assertTrue((result as Result.Error).error is PingPongErrorType.Unexpected)
    }

    private fun givenRepositoryError(error: CommonErrorType, code: ContractErrorCode) {
        every {
            pingPongRepository.createPingPong(any(), any())
        } returns flowOf(Result.Loading, Result.Error(error = error, code = code))
    }

    companion object {
        private const val TEST_USER_KEYWORD_ID = 10L

        private val TestPingPong = PingPong(
            id = PingPongId(1L),
            userKeywordId = UserKeywordId(TEST_USER_KEYWORD_ID),
            questionerId = UserId(2L),
            question = PingPongQuestionContent.create("질문"),
            createdAt = 1_000L,
            updatedAt = 1_000L,
        )
    }
}
