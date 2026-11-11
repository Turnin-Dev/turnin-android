package com.turnin.core.domain.common.error

import com.turnin.core.domain.common.BaseError
import com.turnin.core.domain.common.Result
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ResultErrorMapperTest {
    @Test
    fun `에러 변환 - 서버 에러 코드를 함께 전달한다`() {
        // given
        val result: Result<Unit, CommonErrorType> = Result.Error(
            error = CommonErrorType.Network.NotFound,
            code = TestCode,
        )
        var receivedCode: ContractErrorCode? = null

        // when
        result.mapError { _, code ->
            receivedCode = code
            TestError
        }

        // then
        assertEquals(TestCode, receivedCode)
    }

    @Test
    fun `에러 변환 - 변환 후에도 메시지와 서버 에러 코드를 유지한다`() {
        // given
        val result: Result<Unit, CommonErrorType> = Result.Error(
            error = CommonErrorType.Network.NotFound,
            message = TEST_MESSAGE,
            code = TestCode,
        )

        // when
        val mapped = result.mapError { _ -> TestError }

        // then
        assertEquals(
            Result.Error(error = TestError, message = TEST_MESSAGE, code = TestCode),
            mapped,
        )
    }

    @Test
    fun `에러 변환 - Loading과 Success는 그대로 통과시킨다`() = runTest {
        // given
        val flow = flowOf<Result<Int, CommonErrorType>>(Result.Loading, Result.Success(1))

        // when
        val mapped = flow.mapError { _, _ -> TestError }.toList()

        // then
        assertEquals(listOf(Result.Loading, Result.Success(1)), mapped)
    }

    private data object TestError : BaseError

    companion object {
        private const val TEST_MESSAGE = "message"

        /**
         * 테스트용 서버 에러 코드
         *
         * [ContractErrorCode]는 sealed라 테스트 모듈에서 하위 타입을 만들 수 없으므로 실제 코드를 사용한다.
         * 기본값([ContractErrorCode.Unexpected])이면 "유지"와 "기본값으로 초기화"를 구분할 수 없으므로 다른 코드를 사용한다.
         */
        private val TestCode: ContractErrorCode = ContractErrorCode.PingPong.PP003
    }
}
