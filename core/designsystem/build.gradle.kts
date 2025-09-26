plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.peekr.android.library.compose)
}

android {
    namespace = "com.peekr.core.designsystem"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Essential
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    // Coil
    implementation(libs.coil.compose)

    // Test
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
