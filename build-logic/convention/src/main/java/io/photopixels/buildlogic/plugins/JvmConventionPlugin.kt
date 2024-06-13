import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("core.ktx").get())
                "implementation"(libs.findLibrary("kotlin.coroutines").get())
                "implementation"(libs.findLibrary("kotlin.collections.immutable").get())
            }
        }
    }
}
