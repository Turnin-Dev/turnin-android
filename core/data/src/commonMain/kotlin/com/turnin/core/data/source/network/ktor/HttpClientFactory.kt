package com.turnin.core.data.source.network.ktor

import com.turnin.core.data.source.network.policy.NetworkPolicy
import com.turnin.core.data.source.network.util.networkJson
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json

/**
 * KMP 공용 [HttpClient]를 생성한다.
 *
 * 인증(토큰 첨부·갱신)은 플랫폼이 넘겨주는 [engine]이 담당한다.
 * (Android: 기존 토큰 OkHttpClient를 사용하는 OkHttp 엔진, iOS: Darwin 엔진 + Ktor Auth)
 * 재시도·타임아웃은 Retrofit과 같은 [NetworkPolicy]를 사용한다.
 *
 * @param engine 플랫폼별 HTTP 엔진
 * @param baseUrl 서버 기본 URL
 * @param configure 플랫폼별 추가 설정 (예: iOS의 `installBearerAuth`)
 */
fun createHttpClient(
    engine: HttpClientEngine,
    baseUrl: String,
    configure: HttpClientConfig<*>.() -> Unit = {},
): HttpClient = createHttpClient(engine, baseUrl, retryDelay = null, configure = configure)

/**
 * @param retryDelay 재시도 지연 함수 (테스트에서 지연 시간 검증용, null이면 기본 delay)
 */
internal fun createHttpClient(
    engine: HttpClientEngine,
    baseUrl: String,
    retryDelay: (suspend (Long) -> Unit)?,
    configure: HttpClientConfig<*>.() -> Unit = {},
): HttpClient = HttpClient(engine) {
    // 상태 코드는 ktorNetworkCall에서 직접 처리한다.
    expectSuccess = false

    install(ContentNegotiation) {
        json(networkJson)
    }

    install(HttpTimeout) {
        val timeout = NetworkPolicy.Timeout.Default
        connectTimeoutMillis = timeout.connectMillis
        socketTimeoutMillis = maxOf(timeout.readMillis, timeout.writeMillis)
        requestTimeoutMillis = timeout.callMillis
    }

    install(HttpRequestRetry) {
        maxRetries = NetworkPolicy.Retry.MAX_RETRIES
        retryIf { _, response -> NetworkPolicy.Retry.isRetryableStatus(response.status.value) }
        retryOnExceptionIf { _, cause -> NetworkPolicy.Retry.isRetryableException(cause) }
        // Ktor의 retry 값은 1부터 시작하므로 0부터 시작하는 재시도 순번으로 변환한다.
        delayMillis(respectRetryAfterHeader = false) { retry ->
            NetworkPolicy.Retry.fullJitterDelayMillis(retryIndex = retry - 1)
        }
        retryDelay?.let { delay(it) }
    }

    defaultRequest {
        url(baseUrl)
    }

    configure()
}
