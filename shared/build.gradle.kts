import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/**
 * iOS 앱이 사용하는 KMP umbrella 프레임워크 (Android는 사용하지 않음)
 *
 * - 공유 모듈(:core:data, :domain, :core:domain)을 하나의 프레임워크("Shared")로 묶는다.
 * - iOS 진입점(Koin, Keychain, Darwin 엔진)과 Swift용 Facade만 담당한다.
 * - Swift 연동은 SKIE(Flow → AsyncSequence, sealed/enum → Swift enum)를 사용한다.
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.skie)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "Shared"
            isStatic = true
            binaryOption("bundleId", "com.turnin.shared")
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.domain)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.darwin)
            implementation(libs.koin.core)
        }

        iosTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

skie {
    analytics {
        // 빌드 분석 데이터(하드웨어·git 통계 등)를 수집·전송하지 않는다.
        enabled.set(false)
    }
}
