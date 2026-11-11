package com.turnin.domain.pingPong.usecase

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.common.error.CommonErrorType
import com.turnin.core.domain.common.error.ContractErrorCode
import com.turnin.core.domain.model.PingPongId
import com.turnin.core.domain.pingPong.repository.PingPongRepository
import com.turnin.domain.pingPong.error.PingPongErrorType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeletePingPongAnswerUseCaseTest {
    private val pingPongRepository: PingPongRepository = mockk()
    private val useCase = DeletePingPongAnswerUseCase(pingPongRepository)

    @Test
    fun `답변 삭제 - 성공 시 Success를 반환한다`() = runTest {
        // given
        every {
            pingPongRepository.deletePingPongAnswer(PingPongId(TEST_PING_PONG_ID))
        } returns flowOf(Result.Loading, Result.Success(Unit))

        // when
        val result = useCase(TEST_PING_PONG_ID).last()

        // then
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `답변 삭제 - 서버 에러 코드를 상황별 에러로 변환한다`() = runTest {
        // given
        val expected = mapOf(
            ContractErrorCode.PingPong.PP003 to PingPongErrorType.QuestionAlreadyDeleted,
            ContractErrorCode.PingPong.PP007 to PingPongErrorType.NoPermissionToDeleteAnswer,
            ContractErrorCode.PingPong.PP008 to PingPongErrorType.AnswerAlreadyDeleted,
        )

        // when
        val actual = expected.keys.associateWith { code ->
            every {
                pingPongRepository.deletePingPongAnswer(any())
            } returns flowOf(Result.Error(error = CommonErrorType.Network.NotFound, code = code))
            (useCase(TEST_PING_PONG_ID).last() as Result.Error).error
        }

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `답변 삭제 - 구분되지 않는 에러는 CommonError로 감싸 반환한다`() = runTest {
        // given
        every {
            pingPongRepository.deletePingPongAnswer(any())
        } returns flowOf(Result.Error(error = CommonErrorType.Network.InternalServerError))

        // when
        val result = useCase(TEST_PING_PONG_ID).last()

        // then
        assertEquals(
            PingPongErrorType.CommonError(CommonErrorType.Network.InternalServerError),
            (result as Result.Error).error,
        )
    }

    companion object {
        private const val TEST_PING_PONG_ID = 1L
    }
}
