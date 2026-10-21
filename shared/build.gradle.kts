import co.touchlab.skie.plugin.configuration.SkieExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

/**
 * 공유 모듈 umbrella (composition root)
 *
 * - `:shared:domain`, `:shared:data`, `:shared:presentation`을 조립한다. 기능 코드는 두지 않는다.
 * - 기능별 Koin 모듈 목록(`sharedModules`)을 제공한다. (Android: `:app`의 Koin 브릿지 → Hilt, iOS: `SharedEntryPoint`)
 * - iOS에는 umbrella 프레임워크("Shared") 하나로 제공하며, Swift 연동은 SKIE를 사용한다.
 * - 프로젝트의 Android 모듈에 의존하지 않는다. (Android 모듈이 shared 모듈에 의존한다.)
 */
plugins {
    alias(libs.plugins.turnin.kmp.library)
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
            // Swift에서 사용하는 Facade·상태 모델을 프레임워크 API로 내보낸다.
            export(projects.shared.presentation)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.domain)
            api(projects.shared.data)
            api(projects.shared.presentation)
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
