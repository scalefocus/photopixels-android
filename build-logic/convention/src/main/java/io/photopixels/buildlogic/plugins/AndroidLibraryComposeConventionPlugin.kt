import com.android.build.gradle.LibraryExtension
import io.photopixels.buildlogic.config.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.getByType<LibraryExtension>()

            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            configureAndroidCompose(extension)
        }
    }
}
