plugins {
    alias(libs.plugins.turnin.jvm.library)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.dagger.hilt.javax)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockK)
    testImplementation(libs.kotlinx.coroutines.test)

    // Paging3 (without android dependencies)
    implementation(libs.androidx.paging.common)
}
