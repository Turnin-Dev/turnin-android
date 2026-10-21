/**
 * 공유 presentation 계층 (Android·iOS)
 *
 * - `feature/<기능>/`: iOS용 Facade와 Swift용 상태 모델 (iosMain), 추후 공유 ViewModel
 * - domain에만 의존한다. data 계층에 접근하지 않는다.
 */
plugins {
    alias(libs.plugins.turnin.kmp.library)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        namespace = "com.turnin.shared.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.domain)
        }
    }
}
