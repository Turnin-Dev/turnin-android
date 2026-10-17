package com.turnin.shared

import com.turnin.shared.data.core.auth.KeychainTokenStorage
import com.turnin.shared.data.core.network.auth.TokenStorage
import com.turnin.shared.di.iosModules
import com.turnin.shared.di.iosPlatformModule
import com.turnin.shared.di.sharedModules
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import platform.Foundation.NSUserDefaults

/**
 * iOS 앱의 KMP 진입점 (composition root)
 *
 * 생명주기와 DI 컨테이너만 담당한다. 기능 API는 각 Facade(`AuthFacade`, `AnnouncementFacade` 등)가 제공하며
 * Swift에서는 `SharedEntryPoint.inject()`로 꺼낸다. 기능 추가 시 이 객체는 바뀌지 않는다. (`sharedModules`, `iosModules` 참고)
 *
 * Android Koin 브릿지와 같은 원칙으로 전역 `startKoin` 대신 별도 Koin 컨텍스트를 사용한다.
 *
 * 규칙 예외(전역 싱글톤): Swift가 Kotlin에 들어오는 유일한 진입점이라 object로 둔다. 기능 코드는 이 객체에 접근하지 않고 생성자 주입만 사용한다.
 */
object SharedEntryPoint {
    private var koin: Koin? = null

    /**
     * 공유 모듈을 시작한다. 이미 시작된 경우 무시한다.
     *
     * @param baseUrl 서버 기본 URL
     */
    fun start(baseUrl: String) {
        start(baseUrl = baseUrl, engine = Darwin.create(), tokenStorage = KeychainTokenStorage())
    }

    /**
     * Swift에서 타입으로 객체를 꺼낸다. (Swift 확장 `SharedEntryPoint.inject()`에서 사용)
     *
     * @param objCClass Kotlin 클래스에 대응하는 Objective-C 클래스 (예: `AnnouncementFacade.self`)
     */
    @OptIn(BetaInteropApi::class)
    fun get(objCClass: ObjCClass): Any {
        val kotlinClass = requireNotNull(getOriginalKotlinClass(objCClass)) { "Kotlin 클래스가 아닙니다: $objCClass" }
        return requireKoin().get(kotlinClass)
    }

    internal fun start(baseUrl: String, engine: HttpClientEngine, tokenStorage: TokenStorage) {
        if (koin != null) return
        clearTokensOnFirstLaunch(tokenStorage)
        koin = koinApplication {
            modules(listOf(iosPlatformModule(engine, baseUrl, tokenStorage)) + sharedModules + iosModules)
        }.koin
    }

    internal inline fun <reified T : Any> resolve(): T = requireKoin().get()

    internal fun reset() {
        koin?.close()
        koin = null
    }

    @PublishedApi
    internal fun requireKoin(): Koin = checkNotNull(koin) { "SharedEntryPoint.start(baseUrl:)를 먼저 호출해야 합니다." }

    /** Keychain 항목은 앱을 삭제해도 남으므로, 설치 후 첫 실행 시 이전 토큰을 지운다. */
    private fun clearTokensOnFirstLaunch(tokenStorage: TokenStorage) {
        val defaults = NSUserDefaults.standardUserDefaults
        if (defaults.boolForKey(KEY_HAS_LAUNCHED)) return
        runBlocking { tokenStorage.clear() }
        defaults.setBool(true, forKey = KEY_HAS_LAUNCHED)
    }

    private const val KEY_HAS_LAUNCHED = "com.turnin.shared.hasLaunched"
}
