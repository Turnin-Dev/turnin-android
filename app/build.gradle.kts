import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.peekr.android.application)
    alias(libs.plugins.peekr.android.application.compose)
    alias(libs.plugins.gms)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").inputStream().use(::load)
}

fun Properties.require(name: String): String =
    getProperty(name) ?: error("$name is not defined in local.properties")

val hasReleaseSigning = listOf(
    "STORE_FILE",
    "STORE_PASSWORD",
    "KEY_ALIAS",
    "KEY_PASSWORD",
).all(localProperties::containsKey)

android {
    namespace = "com.peekr.peekrapp"

    defaultConfig {
        applicationId = "com.peekr.peekrapp"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        val kakaoKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY")
            ?: error("KAKAO_NATIVE_APP_KEY is not defined in local.properties")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", kakaoKey)
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey.removeSurrounding("\"")
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(localProperties.require("STORE_FILE"))
                storePassword = localProperties.require("STORE_PASSWORD")
                keyAlias = localProperties.require("KEY_ALIAS")
                keyPassword = localProperties.require("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["crashlyticsEnabled"] = false
        }
        release {
            manifestPlaceholders["crashlyticsEnabled"] = true

            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }

            isMinifyEnabled = true
            isShrinkResources = true
//            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // Crashlytics 매핑 파일 자동 업로드
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // TODO: app 모듈이 data 모듈까지 의존하는 것이 클린아키텍처 방식에서 벗어난다면 추후 별도의 di 모듈을 생성하는 것을 고려
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
    implementation(projects.data)
    implementation(projects.domain)
    implementation(projects.presentation)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coil
    implementation(libs.coil.compose)

    // Kakao SDK
    implementation(libs.kakao.sdk.v2.all)
    implementation(libs.kakao.sdk.v2.user)
    implementation(libs.kakao.sdk.v2.talk)
    implementation(libs.kakao.sdk.v2.share)
    implementation(libs.kakao.sdk.v2.cert)

    // Navigation
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi.kotlin)

    // Splash Screen
    implementation(libs.splash.screen)

    // FCM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Testing
    testImplementation(libs.mockK)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}
