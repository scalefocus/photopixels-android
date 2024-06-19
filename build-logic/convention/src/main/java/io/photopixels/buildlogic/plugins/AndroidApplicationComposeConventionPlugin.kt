import com.android.build.api.dsl.ApplicationExtension
import io.photopixels.buildlogic.config.configureAndroidCompose
import io.photopixels.buildlogic.extensions.findPluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(findPluginId("androidApplication"))
                apply(findPluginId("kotlinComposeCompiler"))
            }

            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
