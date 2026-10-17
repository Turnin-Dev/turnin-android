import com.turnin.app.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configureKotlinJvm()

            // Android 모듈과 같은 이름으로 등록해 CI(`./gradlew testDebugUnitTest`)에서 JVM 모듈 테스트도 함께 실행되도록 한다.
            tasks.register("testDebugUnitTest") {
                group = "verification"
                description = "Runs test (alias for CI)."
                dependsOn("test")
            }
        }
    }
}
