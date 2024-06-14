import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class NetworkingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                val kotlinSerialization = libs.findPlugin("kotlinSerialization")
                    .get().get().pluginId
                apply(kotlinSerialization)
            }

            dependencies {
                //"implementation"(libs.findLibrary("eithernet").get())
                "implementation"(libs.findBundle("ktor").get())
            }
        }
    }
}
