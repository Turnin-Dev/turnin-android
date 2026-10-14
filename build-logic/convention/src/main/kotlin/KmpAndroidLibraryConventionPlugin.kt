import com.android.build.gradle.LibraryExtension
import com.turnin.app.ExtensionType
import com.turnin.app.configureAndroidCommon
import com.turnin.app.configureBuildTypes
import com.turnin.app.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Android 빌드 기능(build type, `BuildConfig`, Hilt Gradle Plugin)이 필요한 KMP 라이브러리 모듈 공통 설정
 *
 * - `kotlin.multiplatform` + `com.android.library` 조합 (Android-KMP 라이브러리 플러그인은 build type·`BuildConfig` 미지원)
 * - 타겟: android(`androidTarget`), iosArm64, iosSimulatorArm64
 * - Android 코드는 `androidMain`, Android 단위 테스트는 `androidUnitTest`
 * - 테스트: `commonTest`에서 kotlin.test + Mokkery + kotlinx-coroutines-test
 * - `namespace`, build type별 `buildConfigField`는 각 모듈의 `android { }` 블록에서 지정한다.
 */
class KmpAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
                apply("dev.mokkery")
            }

            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)

                configureBuildTypes(this, ExtensionType.LIBRARY)

                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget {
                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_17
                    }
                }

                iosArm64()
                iosSimulatorArm64()

                sourceSets.commonTest.dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.findLibrary("kotlinx.coroutines.test").get())
                }
            }
        }
    }
}
