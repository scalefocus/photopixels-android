import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "api"(libs.findLibrary("junit").get())
                "api"(libs.findLibrary("kotlin.test").get())
                "api"(libs.findLibrary("kotlin.testing.coroutines").get())
            }
        }
    }
}
