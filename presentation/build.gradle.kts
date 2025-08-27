plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.peekr.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":designsystem"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // LifeCycle & Navigation
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutine
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // Coil
    implementation(libs.coil.compose)

    // Logging
    implementation(libs.timber)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Local tests: JUnit, Coroutines Test, Android runner, Mockito
    androidTestImplementation(libs.androidx.test.runner)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockK)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
}
