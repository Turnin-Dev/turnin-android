plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.peekr.android.library.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.peekr.core.presentation"
    testFixtures.enable = true
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coil
    implementation(libs.coil.compose)

    // LifeCycle & Navigation
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // Test Fixtures
    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.androidx.test.core)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
    testFixturesImplementation(libs.androidx.lifecycle.viewmodel.compose)
}
