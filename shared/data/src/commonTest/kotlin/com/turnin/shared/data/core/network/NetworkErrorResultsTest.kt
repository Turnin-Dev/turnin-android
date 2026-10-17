package com.turnin.shared.data.core.network

import com.turnin.shared.domain.core.error.NetworkErrorType
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkErrorResultsTest {
    @Test
    fun `HTTP 상태 코드를 네트워크 에러 타입으로 변환한다`() {
        val expected = mapOf(
            400 to NetworkErrorType.Network.BadRequest,
            401 to NetworkErrorType.Network.Unauthorized,
            403 to NetworkErrorType.Network.Forbidden,
            404 to NetworkErrorType.Network.NotFound,
            408 to NetworkErrorType.Network.RequestTimeout,
            409 to NetworkErrorType.Network.Conflict,
            422 to NetworkErrorType.Network.ClientError(422),
            500 to NetworkErrorType.Network.InternalServerError,
            502 to NetworkErrorType.Network.BadGateway,
            503 to NetworkErrorType.Network.ServiceUnavailable,
            504 to NetworkErrorType.Network.GatewayTimeout,
            501 to NetworkErrorType.Network.ServerError(501),
            302 to NetworkErrorType.Unexpected(null),
        )

        expected.forEach { (status, errorType) ->
            assertEquals(errorType, status.toNetworkErrorType(), "status=$status")
        }
    }

    @Test
    fun `에러 바디가 통합 에러 응답 형식이면 코드와 메시지를 담는다`() {
        val result = httpErrorResult(404, """{"code":"N001","message":"없음","status":404}""")

        assertEquals(NetworkErrorType.Network.NotFound, result.error)
        assertEquals("N001", result.code)
        assertEquals("없음", result.message)
        assertEquals(404, result.status)
    }

    @Test
    fun `예외 메시지의 IP 주소는 마스킹한다`() {
        val result = exceptionErrorResult(
            NetworkErrorType.Network.ConnectionFailed,
            IllegalStateException("failed to connect to /192.168.0.1"),
        )

        assertEquals("failed to connect to /XXX.XXX.XXX.XXX", result.message)
    }
}
