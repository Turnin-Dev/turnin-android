/**
 * 공유 data 계층 (Android·iOS)
 *
 * - `core/`: HttpClient, NetworkPolicy, BearerAuth, TokenStorage / `feature/<기능>/`: dto, datasource, repository 구현, Koin data 모듈
 * - 외부 모델(DTO)과 예외는 이 모듈에서 domain 모델·에러 타입으로 변환한다.
 */
plugins {
    alias(libs.plugins.turnin.kmp.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.shared.data"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.domain)

            // 공개 API(createHttpClient, Koin Module)에 노출되는 타입은 api로 선언한다.
            api(libs.ktor.client.core)
            api(libs.koin.core)

            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            // createHttpClient(OkHttpClient, ...)의 공개 API에 노출된다.
            api(libs.okhttp)
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
