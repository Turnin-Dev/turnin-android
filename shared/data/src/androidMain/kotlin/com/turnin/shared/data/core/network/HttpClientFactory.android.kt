package com.turnin.shared.data.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient

/**
 * Android 앱의 [OkHttpClient]를 엔진으로 사용하는 [HttpClient]를 생성한다.
 *
 * 토큰 첨부·갱신, 캐시, 로깅은 넘겨받은 [okHttpClient](`@TokenOkHttpClient`)가 담당하므로 Retrofit과 같은 동작을 유지한다.
 *
 * @param okHttpClient 인증이 적용된 OkHttpClient
 * @param baseUrl 서버 기본 URL
 */
fun createHttpClient(okHttpClient: OkHttpClient, baseUrl: String): HttpClient =
    createHttpClient(engine = OkHttp.create { preconfigured = okHttpClient }, baseUrl = baseUrl)
