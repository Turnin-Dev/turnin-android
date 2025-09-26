import com.android.build.api.dsl.ApplicationExtension
import com.peekr.peekrapp.ExtensionType
import com.peekr.peekrapp.configureBuildTypes
import com.peekr.peekrapp.configureKotlinAndroid
import com.peekr.peekrapp.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().requiredVersion.toInt()
                    minSdk = libs.findVersion("projectMinSdkVersion").get().requiredVersion.toInt()
                    compileSdk = libs.findVersion("projectCompileSdkVersion").get().requiredVersion.toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().requiredVersion.toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                configureKotlinAndroid(this)

                configureBuildTypes(this, ExtensionType.APPLICATION)
            }
        }
    }
}
