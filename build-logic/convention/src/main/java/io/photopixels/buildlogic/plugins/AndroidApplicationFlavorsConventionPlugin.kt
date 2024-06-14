import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import io.photopixels.buildlogic.config.manageFlavors
import io.photopixels.buildlogic.extensions.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.withPlugin(findPluginId("androidApplication")) {
                extensions.configure<ApplicationExtension> {
                    manageFlavors(this)
                }
            }

            pluginManager.withPlugin(findPluginId("androidLibrary")) {
                extensions.configure<LibraryExtension> {
                    manageFlavors(this)
                }
            }
        }
    }
}
