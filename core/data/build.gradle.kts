import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Enable room auto-migrations
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.peekr.core.data"

    defaultConfig {
        val properties = Properties().apply { load(FileInputStream(rootProject.file("local.properties"))) }
        buildConfigField("String", "PEEKR_DATA_STORE", properties.getProperty("PEEKR_DATA_STORE"))
        buildConfigField("String", "PEEKR_MOCK_SERVER_URL", properties.getProperty("PEEKR_MOCK_SERVER_URL"))
        buildConfigField("String", "PEEKR_LOCAL_SERVER_URL", properties.getProperty("PEEKR_LOCAL_SERVER_URL"))
        buildConfigField("String", "PEEKR_REAL_SERVER_URL", properties.getProperty("PEEKR_REAL_SERVER_URL"))
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", properties.getProperty("GOOGLE_WEB_CLIENT_ID"))
        buildConfigField("String", "CLOUD_STORAGE_SERVER_URL", properties.getProperty("CLOUD_STORAGE_SERVER_URL"))
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
    api(libs.moshi.kotlin)

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
