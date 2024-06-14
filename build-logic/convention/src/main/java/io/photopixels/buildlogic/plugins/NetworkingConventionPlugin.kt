import io.photopixels.buildlogic.extensions.findPluginId
import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class NetworkingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(findPluginId("kotlinSerialization"))
            }

            dependencies {
                //"implementation"(libs.findLibrary("eithernet").get())
                "implementation"(libs.findBundle("ktor").get())
            }
        }
    }
}
