package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.model.PingPongAnswerId
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.pingPong.model.PingPongAnswer
import com.turnin.core.domain.pingPong.model.PingPongAnswerContent
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CreatePingPongAnswerUseCaseTest {
    private val pingPongRepository: PingPongRepository = mockk()
    private val useCase = CreatePingPongAnswerUseCase(pingPongRepository)

    @Test
    fun `답변 작성 - 성공 시 등록된 답변을 반환한다`() = runTest {
        // given
        every { pingPongRepository.createAnswer(any(), any()) } returns flowOf(Result.Success(TestAnswer))

        // when
        val result = useCase(TEST_PING_PONG_ID, "답변").last()

        // then
        assertEquals(Result.Success(TestAnswer), result)
    }

    @Test
    fun `답변 작성 - 공백만 입력하면 저장소를 호출하지 않고 InvalidAnswer 에러를 반환한다`() = runTest {
        // when
        val result = useCase(TEST_PING_PONG_ID, "   ").last()

        // then
        assertEquals(PingPongErrorType.InvalidAnswer, (result as Result.Error).error)
        verify(exactly = 0) { pingPongRepository.createAnswer(any(), any()) }
    }

    @Test
    fun `답변 작성 - 서버 에러 코드를 상황별 에러로 변환한다`() = runTest {
        // given
        val expected = mapOf(
            ContractErrorCode.PingPong.PP001 to PingPongErrorType.UserKeywordNotFound,
            ContractErrorCode.PingPong.PP003 to PingPongErrorType.QuestionNotFound,
            ContractErrorCode.PingPong.PP004 to PingPongErrorType.NotUserKeywordOwner,
            ContractErrorCode.PingPong.PP005 to PingPongErrorType.AlreadyAnswered,
            ContractErrorCode.PingPong.PP006 to PingPongErrorType.CannotAnswerBlockedQuestioner,
        )

        // when
        val actual = expected.keys.associateWith { code ->
            every {
                pingPongRepository.createAnswer(any(), any())
            } returns flowOf(Result.Error(error = CommonErrorType.Network.Forbidden, code = code))
            (useCase(TEST_PING_PONG_ID, "답변").last() as Result.Error).error
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `답변 작성 - 구분되지 않는 에러는 CommonError로 감싸 반환한다`() = runTest {
        // given
        every {
            pingPongRepository.createAnswer(any(), any())
        } returns flowOf(Result.Error(error = CommonErrorType.Network.InternalServerError))

        // when
        val result = useCase(TEST_PING_PONG_ID, "답변").last()

        // then
        assertEquals(
            PingPongErrorType.CommonError(CommonErrorType.Network.InternalServerError),
            (result as Result.Error).error,
        )
    }

    companion object {
        private const val TEST_PING_PONG_ID = 1L

        private val TestAnswer = PingPongAnswer(
            id = PingPongAnswerId(3L),
            pingPongId = PingPongId(TEST_PING_PONG_ID),
            answer = PingPongAnswerContent("답변"),
            createdAt = 2_000L,
            updatedAt = 2_000L,
        )
    }
}
