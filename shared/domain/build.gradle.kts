/**
 * 공유 domain 계층 (Android·iOS)
 *
 * - `core/`: Result, 공통 에러 타입 / `feature/<기능>/`: model, repository 인터페이스, error, usecase
 * - 순수 Kotlin만 사용한다. 네트워크·DB·DI 프레임워크에 의존하지 않는다.
 */
plugins {
    alias(libs.plugins.turnin.kmp.library)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.shared.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
