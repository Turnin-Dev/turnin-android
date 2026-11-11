package com.turnin.core.data.source.network.error

import com.turnin.core.domain.common.error.CommonErrorType
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkErrorTypeMapperTest {
    @Test
    fun `HTTP 상태코드 429는 TooManyRequests로 변환한다`() {
        // given
        val networkError = NetworkErrorType.Network.HttpError(429)

        // when
        val commonError = networkError.toCommonErrorType()

        // then
        assertEquals(CommonErrorType.Network.TooManyRequests, commonError)
    }

    @Test
    fun `따로 정의하지 않은 4xx 상태코드는 ClientError로 변환한다`() {
        // given
        val networkError = NetworkErrorType.Network.HttpError(418)

        // when
        val commonError = networkError.toCommonErrorType()

        // then
        assertEquals(CommonErrorType.Network.ClientError(418), commonError)
    }
}
