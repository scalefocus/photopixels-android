import com.android.build.gradle.LibraryExtension
import io.photopixels.buildlogic.config.configureAndroidCompose
import io.photopixels.buildlogic.extensions.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(findPluginId("kotlinComposeCompiler"))

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}
