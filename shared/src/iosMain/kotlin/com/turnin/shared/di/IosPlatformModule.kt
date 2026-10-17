package com.turnin.shared.di

import com.turnin.shared.core.auth.AuthEvents
import com.turnin.shared.data.core.di.PlatformQualifier
import com.turnin.shared.data.core.network.auth.TokenStorage
import com.turnin.shared.data.core.network.auth.installBearerAuth
import com.turnin.shared.data.core.network.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS 플랫폼 재료 모듈 ([PlatformQualifier]로 공유 기능 모듈에 제공)
 *
 * Koin은 반환 타입으로 조회하므로 타입을 명시한다.
 *
 * @param engine HTTP 엔진 (앱: Darwin, 테스트: MockEngine)
 * @param baseUrl 서버 기본 URL
 * @param tokenStorage 토큰 저장소 (앱: Keychain)
 */
internal fun iosPlatformModule(
    engine: HttpClientEngine,
    baseUrl: String,
    tokenStorage: TokenStorage,
): Module = module {
    single<TokenStorage> { tokenStorage }
    single<HttpClient>(PlatformQualifier.AuthHttpClient) {
        val authEvents = get<AuthEvents>()
        createHttpClient(engine, baseUrl) {
            installBearerAuth(tokenStorage = get(), onAuthFailure = { authEvents.notifyAuthFailure() })
        }
    }
    single<CoroutineDispatcher>(PlatformQualifier.IoDispatcher) { Dispatchers.IO }
}
