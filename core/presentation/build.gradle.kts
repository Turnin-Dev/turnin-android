plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.peekr.android.library.compose)
}

android {
    namespace = "com.peekr.core.presentation"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
}
