import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.peekr.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.team.data.TestRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = Properties().apply { load(FileInputStream(rootProject.file("local.properties"))) }
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", properties.getProperty("GOOGLE_WEB_CLIENT_ID"))
        buildConfigField("String", "PEEKR_MOCK_SERVER_URL", properties.getProperty("PEEKR_MOCK_SERVER_URL"))
        buildConfigField("String", "PEEKR_LOCAL_SERVER_URL", properties.getProperty("PEEKR_LOCAL_SERVER_URL"))
        buildConfigField("String", "PEEKR_REAL_SERVER_URL", properties.getProperty("PEEKR_REAL_SERVER_URL"))
        buildConfigField("String", "PEEKR_DATA_STORE", properties.getProperty("PEEKR_DATA_STORE"))
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

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    // Testing
    kspTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.hilt.android.testing)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    // Kakao SDK
    implementation(libs.kakao.sdk.v2.all)
    implementation(libs.kakao.sdk.v2.user)
    implementation(libs.kakao.sdk.v2.talk)
    implementation(libs.kakao.sdk.v2.share)
    implementation(libs.kakao.sdk.v2.cert)

    // Logging
    implementation(libs.timber)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    testImplementation(libs.retrofit.mock)
    testImplementation(libs.okhttp.mockWebServer)

    // Serialization
    implementation(libs.moshi.kotlin)

    // DataStore
    implementation(libs.androidx.dataStore.preference)

    // Testing: JUnit, Coroutines Test, Android runner, Mockito
    androidTestImplementation(libs.androidx.test.runner)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockK)
    testImplementation(libs.robolectric)
}
