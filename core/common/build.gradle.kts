plugins {
    alias(libs.plugins.peekr.android.library)
}

android {
    namespace = "com.peekr.core.common"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
}
