import io.photopixels.buildlogic.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                // hilt dependencies
                "implementation"(libs.findLibrary("hilt.android").get())
                "ksp"(libs.findLibrary("hilt.compiler").get())
                "ksp"(libs.findLibrary("hilt.android.compiler").get())
                "kspAndroidTest"(libs.findLibrary("hilt.compiler").get())

                // androidx hilt extensions
                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.hilt.common").get())
                "implementation"(libs.findLibrary("androidx.hilt.work").get())
                "ksp"(libs.findLibrary("androidx.hilt.compiler").get())
            }
        }
    }
}
