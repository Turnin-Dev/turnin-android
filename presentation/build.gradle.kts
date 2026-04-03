import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.peekr.android.library.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.peekr.presentation"

    defaultConfig {
        val properties = Properties().apply {
            rootProject.file("local.properties").inputStream().use { load(it) }
        }
        val qnaUrl = properties.getProperty("QNA_URL") ?: "\"\""
        buildConfigField("String", "QNA_URL", qnaUrl)
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.presentation)
    testImplementation(testFixtures(projects.core.presentation))
    implementation(projects.core.designsystem)
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.graphics)
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

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Paging3
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    testImplementation(libs.androidx.paging.common)
    testImplementation(libs.androidx.paging.testing)

    // Kakao SDK
    implementation(libs.kakao.sdk.v2.user)

    // Local tests: JUnit, Coroutines Test, Android runner, Mockito
    androidTestImplementation(libs.androidx.test.runner)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.mockK)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
}
