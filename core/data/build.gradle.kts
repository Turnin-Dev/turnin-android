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

    defaultConfig {
        buildConfigField("String", "TURNIN_DATA_STORE", localProperties.getProperty("TURNIN_DATA_STORE"))
        buildConfigField("String", "TURNIN_MOCK_SERVER_URL", localProperties.getProperty("TURNIN_MOCK_SERVER_URL"))
        buildConfigField("String", "TURNIN_LOCAL_SERVER_URL", localProperties.getProperty("TURNIN_LOCAL_SERVER_URL"))
        buildConfigField("String", "TURNIN_REAL_SERVER_URL", localProperties.getProperty("TURNIN_REAL_SERVER_URL"))
        buildConfigField("String", "CLOUD_STORAGE_SERVER_URL", localProperties.getProperty("CLOUD_STORAGE_SERVER_URL"))
    }

    buildTypes {
        debug {
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", localProperties.getProperty("DEV_GOOGLE_WEB_CLIENT_ID"))
        }
        release {
            buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", localProperties.getProperty("PROD_GOOGLE_WEB_CLIENT_ID"))
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
