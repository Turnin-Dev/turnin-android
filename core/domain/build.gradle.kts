plugins {
    alias(libs.plugins.peekr.jvm.library)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.dagger.hilt.javax)
}
