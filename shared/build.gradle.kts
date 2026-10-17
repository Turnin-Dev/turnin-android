import co.touchlab.skie.plugin.configuration.SkieExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

/**
 * Android·iOS 공유 모듈
 *
 * - 프로젝트의 다른 모듈에 의존하지 않는다. (Android 모듈이 이 모듈에 의존한다.)
 * - 기능별 Koin 모듈(`SharedModules`)을 제공한다. (Android: `:app`의 Koin 브릿지 → Hilt, iOS: `SharedSdk`)
 * - iOS에는 umbrella 프레임워크("Shared")로 제공하며, Swift 연동은 SKIE를 사용한다.
 */
plugins {
    alias(libs.plugins.turnin.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.shared"
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
            binaryOption("bundleId", "com.turnin.shared")
        }
    }

    sourceSets {
        commonMain.dependencies {
            // 공개 API(Flow, HttpClient, Koin Module)에 노출되는 타입은 api로 선언한다.
            api(libs.kotlinx.coroutines.core)
            api(libs.ktor.client.core)
            api(libs.koin.core)

            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

// SKIE는 iOS 프레임워크 빌드에만 필요하므로 Xcode가 있는 macOS에서만 적용한다. (Linux CI 영향 차단)
if (HostManager.hostIsMac) {
    apply(plugin = libs.plugins.skie.get().pluginId)

    extensions.configure<SkieExtension> {
        analytics {
            // 빌드 분석 데이터(하드웨어·git 통계 등)를 수집·전송하지 않는다.
            enabled.set(false)
        }
    }
}
