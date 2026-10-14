plugins {
    alias(libs.plugins.turnin.kmp.library)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.core.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // Paging3 (without android dependencies)
            implementation(libs.androidx.paging.common)
        }
    }
}
