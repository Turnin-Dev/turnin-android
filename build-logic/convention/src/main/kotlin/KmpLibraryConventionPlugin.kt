import com.android.build.api.dsl.androidLibrary
import com.turnin.app.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP 라이브러리 모듈 공통 설정
 *
 * - 타겟: android(`com.android.kotlin.multiplatform.library`), iosArm64, iosSimulatorArm64
 * - Android 호스트 테스트(`androidHostTest`) 활성화
 * - `namespace`는 각 모듈의 `kotlin { androidLibrary { } }` 블록에서 지정한다.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.kotlin.multiplatform.library")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidLibrary {
                    compileSdk = libs.findVersion("projectCompileSdkVersion").get().requiredVersion.toInt()
                    minSdk = libs.findVersion("projectMinSdkVersion").get().requiredVersion.toInt()

                    compilerOptions {
                        jvmTarget = JvmTarget.JVM_17
                    }

                    withHostTestBuilder {}
                }

                iosArm64()
                iosSimulatorArm64()

                sourceSets.commonTest.dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}
