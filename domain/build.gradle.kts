plugins {
    alias(libs.plugins.turnin.kmp.library)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.domain"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)

            // Paging3 (without android dependencies)
            implementation(libs.androidx.paging.common)
        }
    }
}
