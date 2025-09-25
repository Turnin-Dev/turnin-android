plugins {
    alias(libs.plugins.peekr.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.peekr.core.data"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.core.domain)

    implementation(libs.timber)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
}
