import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.turnin.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Enable room auto-migrations
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.turnin.core.data"

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_WEB_CLIENT_ID",
                "\"${localProperties.getProperty("DEBUG_GOOGLE_WEB_CLIENT_ID")}\"",
            )
            buildConfigField(
                "String",
                "TURNIN_DATA_STORE",
                "\"${localProperties.getProperty("DEBUG_TURNIN_DATA_STORE")}\"",
            )
            buildConfigField(
                "String",
                "TURNIN_SERVER_URL",
                "\"${localProperties.getProperty("DEBUG_TURNIN_SERVER_URL")}\"",
            )
            buildConfigField(
                "String",
                "CLOUD_STORAGE_SERVER_URL",
                "\"${localProperties.getProperty("DEBUG_CLOUD_STORAGE_SERVER_URL")}\"",
            )
            buildConfigField(
                "String",
                "TURNIN_MOCK_SERVER_URL",
                "\"${localProperties.getProperty("MOCK_TURNIN_SERVER_URL")}\"",
            )
        }
        release {
            val isReleaseTest = localProperties.getProperty("IS_RELEASE_TEST").toBoolean()
            // 릴리즈 테스트 모드(로컬)에선 디버그(개발) 환경으로 실행
            if (isReleaseTest) {
                buildConfigField(
                    "String",
                    "GOOGLE_WEB_CLIENT_ID",
                    "\"${localProperties.getProperty("DEBUG_GOOGLE_WEB_CLIENT_ID")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_DATA_STORE",
                    "\"${localProperties.getProperty("DEBUG_TURNIN_DATA_STORE")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_SERVER_URL",
                    "\"${localProperties.getProperty("DEBUG_TURNIN_SERVER_URL")}\"",
                )
                buildConfigField(
                    "String",
                    "CLOUD_STORAGE_SERVER_URL",
                    "\"${localProperties.getProperty("DEBUG_CLOUD_STORAGE_SERVER_URL")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_MOCK_SERVER_URL",
                    "\"${localProperties.getProperty("MOCK_TURNIN_SERVER_URL")}\"",
                )
            } else {
                buildConfigField(
                    "String",
                    "GOOGLE_WEB_CLIENT_ID",
                    "\"${localProperties.getProperty("RELEASE_GOOGLE_WEB_CLIENT_ID")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_DATA_STORE",
                    "\"${localProperties.getProperty("RELEASE_TURNIN_DATA_STORE")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_SERVER_URL",
                    "\"${localProperties.getProperty("RELEASE_TURNIN_SERVER_URL")}\"",
                )
                buildConfigField(
                    "String",
                    "CLOUD_STORAGE_SERVER_URL",
                    "\"${localProperties.getProperty("RELEASE_CLOUD_STORAGE_SERVER_URL")}\"",
                )
                buildConfigField(
                    "String",
                    "TURNIN_MOCK_SERVER_URL",
                    "\"${localProperties.getProperty("MOCK_TURNIN_SERVER_URL")}\"",
                )
            }
        }
        create("releaseTest") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")

            buildConfigField(
                "String",
                "GOOGLE_WEB_CLIENT_ID",
                "\"${localProperties.getProperty("RELEASE_TEST_GOOGLE_WEB_CLIENT_ID")}\"",
            )
            // 방어 설정 추가
            buildConfigField(
                "String",
                "TURNIN_DATA_STORE",
                "\"${localProperties.getProperty("DEBUG_TURNIN_DATA_STORE")}\"",
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true

            all { test ->
                // when logging required
//                test.systemProperties["robolectric.logging.enabled"] = "true"
                test.systemProperty("robolectric.dependency.repo.url", "https://repo.maven.apache.org/maven2")
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.android.compiler)

    // DataStore
    implementation(libs.androidx.dataStore.preference)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.retrofit.mock)
    testImplementation(libs.okhttp.mockWebServer)

    // Serialization
    implementation(libs.moshi.kotlin)

    // Paging3
    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.androidx.paging.testing)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    // Kakao SDK
    implementation(libs.kakao.sdk.v2.user)

    // FCM
    implementation(libs.firebase.messaging)

    // Testing: JUnit, Coroutines Test, Android runner, Mockito
    androidTestImplementation(libs.androidx.test.runner)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockK)
    testImplementation(libs.robolectric)
}
